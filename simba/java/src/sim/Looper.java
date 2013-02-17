package sim;

import static utils.assertions.Asserter.*;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import sim.collectors.IntervalCollector;
import sim.event_handling.IEventQueue;
import sim.events.Event;
import sim.events.Finish;
import sim.events.NoOp;
import sim.events.Submit;
import sim.model.Job;
import sim.scheduling.Scheduler;
import sim.scheduling.waiting_queue.WaitingQueue;

public class Looper
{

	private static final Logger log = Logger.getLogger(Looper.class);
	private long timeToLogPassed;
	private final Clock clock;
	private final IEventQueue eventQueue;
	private IntervalCollector intervalCollector;
	private final JobFinisher jobFinisher;
	private boolean firstCycle = true;
	private boolean hasEventsNotScheduleYet = true;
	private final SimbaConfiguration simbaConfiguration;
	private WaitingQueue waitingQueue;
	private final Scheduler scheduler;

	@Inject
	public Looper(Clock clock, IEventQueue eventQueue, WaitingQueue waitingQueue, Scheduler scheduler, IntervalCollector hostCollector, JobFinisher jobFinisher, SimbaConfiguration simbaConsts)
	{
		this.waitingQueue = waitingQueue;
		this.scheduler = scheduler;
		this.eventQueue = eventQueue;
		this.clock = clock;
		this.intervalCollector = hostCollector;
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
		intervalCollector.finish();
		jobFinisher.finishExecution();
	}

	boolean tick()
	{
		long time = clock.tick();
		if (isBucketTime(time))
		{
			removeAllRunningJobs();
		}
		boolean handeledEvents = handleEvents(time);
		int scheduledJobs = 0;
		hasEventsNotScheduleYet = hasEventsNotScheduleYet || handeledEvents;
		if (((time % simbaConfiguration.timeToSchedule() == 0 && hasEventsNotScheduleYet) || (time % simbaConfiguration.timeToSchedule() == 1 && firstCycle)) && (!simbaConfiguration.isBucketSimulation() || isBucketTime(time)))
		{
			scheduledJobs = schedule(time);
			if (scheduledJobs == 0)
			{
				hasEventsNotScheduleYet = false;
			}
		}
		intervalCollector.collect(time, handeledEvents, scheduledJobs);
		if (isBucketTime(time))
		{
			log.info("schduled jobs " + scheduledJobs);
			removeAllWaitingJobs();
		}
		if (time % simbaConfiguration.timeToLog() == 0 || firstCycle)
		{
			timeToLogPassed++;
			log.info("===>>> " + timeToLogPassed + " days, events left " + eventQueue.size() + ", waiting jobs " + sizeOfWaitingQueue());
		}
		boolean shouldContinue = firstCycle || handeledEvents || !(eventQueue.isEmpty());
		firstCycle = false;
		return shouldContinue;
	}

	private boolean isBucketTime(long time)
	{
		return simbaConfiguration.isBucketSimulation() && time % simbaConfiguration.bucketSize() == 0;
	}

	private void removeAllRunningJobs()
	{
		List<Finish> l = eventQueue.clearRunningJobs();
		Iterator<Finish> it = l.iterator();
		while (it.hasNext())
		{
			Finish finish = it.next();
			jobFinisher.finish(finish);
		}
		log.info("removed running jobs " + l.size());
	}

	private void removeAllWaitingJobs()
	{
		int i = 0;
		Iterator<Job> it = waitingQueue.iterator();
		while (it.hasNext())
		{
			it.next();
			it.remove();
			i++;
		}
		log.info("removed waiting jobs " + i);
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