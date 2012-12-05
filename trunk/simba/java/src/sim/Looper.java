package sim;

import static utils.assertions.Asserter.*;

import java.util.Iterator;

import org.apache.log4j.Logger;

import sim.collectors.IntervalCollector;
import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.events.Finish;
import sim.events.NoOp;
import sim.events.Submit;

import com.google.inject.assistedinject.Assisted;

public abstract class Looper
{

	private static final Logger log = Logger.getLogger(CentralizedLooper.class);
	private long timeToLogPassed;
	private final Clock clock;
	private final EventQueue eventQueue;
	private IntervalCollector hostCollector;
	private final JobFinisher jobFinisher;
	private boolean firstCycle = true;
	private boolean hasEventsNotScheduleYet = true;
	private final SimbaConfiguration simbaConsts;

	public Looper(@Assisted Clock clock, @Assisted EventQueue eventQueue, @Assisted IntervalCollector hostCollector, @Assisted JobFinisher jobFinisher,
			SimbaConfiguration simbaConsts)
	{
		this.eventQueue = eventQueue;
		this.clock = clock;
		this.hostCollector = hostCollector;
		this.jobFinisher = jobFinisher;
		this.simbaConsts = simbaConsts;
	}

	public void execute()
	{
		boolean shouldContinue = true;
		while (shouldContinue)
		{
			shouldContinue = tick();
		}
		log.info("execute() - loop finished, calling finish()");
		finish();
		asserter().throwsError().assertTrue(sizeOfWaitingQueue() == 0, "waitingQueue not empty, size: " + sizeOfWaitingQueue());
	}

	private void finish()
	{
		hostCollector.finish();
		jobFinisher.finishExecution();
	}

	protected boolean tick()
	{
		long time = clock.tick();
		if (simbaConsts.isBucketSimulation() && time % simbaConsts.bucketSize() == 0)
		{
			removeAllRunningJobs();
		}
		boolean handeledEvents = handleEvents(time);
		int scheduledJobs = 0;
		hasEventsNotScheduleYet = hasEventsNotScheduleYet || handeledEvents;
		if (time % simbaConsts.timeToSchedule() == 0 && hasEventsNotScheduleYet || time % simbaConsts.timeToSchedule() == 1 && firstCycle)
		{
			scheduledJobs = schedule(time);
			hasEventsNotScheduleYet = false;
		}
		if (time % simbaConsts.bucketSize() == 0)
		{
			log.info("schduled jobs " + scheduledJobs);
		}
		hostCollector.collect(time, handeledEvents, scheduledJobs);
		if (time % simbaConsts.timeToLog() == 0 || firstCycle)
		{
			timeToLogPassed++;
			log.info("tick() - time passed " + timeToLogPassed + " days events left " + eventQueue.size() + " waiting jobs " + sizeOfWaitingQueue());
		}
		boolean shouldContinue = firstCycle || handeledEvents || !(eventQueue.isEmpty());
		firstCycle = false;
		return shouldContinue;
	}

	private void removeAllRunningJobs()
	{
		int i = 0;
		Iterator<Event> it = eventQueue.iterator();
		while (it.hasNext())
		{
			Event event = it.next();
			if (event instanceof Finish)
			{
				Finish finish = (Finish) event;
				jobFinisher.finish(finish);
				i++;
				it.remove();
			}
		}
		log.info("removed jobs " + i);
	}

	private boolean handleEvents(long time)
	{
		boolean $ = false;
		while (eventQueue.peek() != null && eventQueue.peek().time() == time)
		{
			Event event = eventQueue.removeFirst();
			if (event instanceof Submit)
			{
				$ = submitJob(event);
			}
			else if (event instanceof Finish)
			{
				$ = true;
				Finish finish = (Finish) event;
				jobFinisher.finish(finish);
			}
			else
			{
				asserter().assertTrue(event instanceof NoOp, "no handle for event " + event);
			}
		}
		return $;
	}

	protected abstract boolean submitJob(Event event);

	protected abstract int schedule(long time);

	protected abstract int sizeOfWaitingQueue();

}