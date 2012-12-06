package sim;

import static utils.assertions.Asserter.*;

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import sim.collectors.IntervalCollector;
import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.events.Finish;
import sim.events.NoOp;
import sim.events.Submit;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.Scheduler;

import com.google.inject.assistedinject.Assisted;

public class Looper
{

	private static final Logger log = Logger.getLogger(Looper.class);
	private long timeToLogPassed;
	private final Clock clock;
	private final EventQueue eventQueue;
	private IntervalCollector hostCollector;
	private final JobFinisher jobFinisher;
	private boolean firstCycle = true;
	private boolean hasEventsNotScheduleYet = true;
	private final SimbaConfiguration simbaConfiguration;
	private AbstractWaitingQueue waitingQueue;
	private final Scheduler scheduler;

	@Inject
	public Looper(@Assisted Clock clock, @Assisted EventQueue eventQueue, @Assisted AbstractWaitingQueue waitingQueue, @Assisted Scheduler scheduler,
			@Assisted IntervalCollector hostCollector, @Assisted JobFinisher jobFinisher, SimbaConfiguration simbaConsts)
	{
		this.waitingQueue = waitingQueue;
		this.scheduler = scheduler;
		this.eventQueue = eventQueue;
		this.clock = clock;
		this.hostCollector = hostCollector;
		this.jobFinisher = jobFinisher;
		this.simbaConfiguration = simbaConsts;
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
		asserter().throwsError().assertTrue(waitingQueue.isEmpty(), "waitingQueue not empty, size: " + sizeOfWaitingQueue());
	}

	private void finish()
	{
		hostCollector.finish();
		jobFinisher.finishExecution();
	}

	boolean tick()
	{
		long time = clock.tick();
		if (simbaConfiguration.isBucketSimulation() && time % simbaConfiguration.bucketSize() == 0)
		{
			removeAllRunningJobs();
		}
		boolean handeledEvents = handleEvents(time);
		int scheduledJobs = 0;
		hasEventsNotScheduleYet = hasEventsNotScheduleYet || handeledEvents;
		if ((time % simbaConfiguration.timeToSchedule() == 0 && hasEventsNotScheduleYet) || (time % simbaConfiguration.timeToSchedule() == 1 && firstCycle))
		{
			scheduledJobs = schedule(time);
			if (scheduledJobs == 0)
			{
				hasEventsNotScheduleYet = false;
			}
		}
		if (time % simbaConfiguration.bucketSize() == 0)
		{
			log.info("schduled jobs " + scheduledJobs);
		}
		hostCollector.collect(time, handeledEvents, scheduledJobs);
		if (time % simbaConfiguration.timeToLog() == 0 || firstCycle)
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

	private int schedule(long time)
	{
		int scheduledJobs;
		scheduledJobs = scheduler.schedule(time);
		return scheduledJobs;
	}

	private boolean submitJob(Event event)
	{
		boolean $ = false;
		Submit submit = (Submit) event;
		Job job = submit.job();
		waitingQueue.add(job);
		if (sizeOfWaitingQueue() < simbaConfiguration.jobsCheckedBySchduler())
		{
			$ = true;
		}
		return $;
	}

	private int sizeOfWaitingQueue()
	{
		return waitingQueue.size();
	}

}