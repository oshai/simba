package sim;

import static utils.assertions.Asserter.*;

import org.apache.log4j.Logger;

import sim.collectors.HostCollector;
import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.events.Finish;
import sim.events.NoOp;
import sim.events.Submit;
import sim.model.Job;
import sim.scheduling.Scheduler;
import sim.scheduling.WaitingQueue;

public class Looper
{
	private static final Logger log = Logger.getLogger(Looper.class);
	private long timeToLog = 60 * 60 * 24;// 1 day
	private long timeToLogPassed;
	private final Clock clock;
	private final EventQueue eventQueue;
	private final WaitingQueue waitingQueue;
	private final Scheduler scheduler;
	private HostCollector hostCollector;
	private final JobFinisher jobFinisher;
	
	public Looper(Clock clock, EventQueue eventQueue, WaitingQueue waitingQueue, Scheduler scheduler, HostCollector hostCollector, JobFinisher jobFinisher)
	{
		this.scheduler = scheduler;
		this.waitingQueue = waitingQueue;
		this.eventQueue = eventQueue;
		this.clock = clock;
		this.hostCollector = hostCollector;
		this.jobFinisher = jobFinisher;
	}
	
	public void execute()
	{
		while (!eventQueue.isEmpty())
		{
			tick();
		}
		log.info("execute() - loop finished, calling finish()");
		finish();
	}
	
	private void finish()
	{
		hostCollector.finish();
		jobFinisher.finishExecution();
	}
	
	boolean tick()
	{
		long time = clock.tick();
		boolean handeledEvents = handleEvents(time);
		if (handeledEvents)
		{
			scheduler.schedule(time);
		}
		hostCollector.collect(time);
		if (time % timeToLog == 0)
		{
			timeToLogPassed++;
			log.info("tick() - time passed " + timeToLogPassed + " events left " + eventQueue.size());
		}
		return handeledEvents;
	}
	
	private boolean handleEvents(long time)
	{
		boolean $ = false;
		while (eventQueue.peek() != null && eventQueue.peek().time() == time)
		{
			$ = true;
			Event event = eventQueue.removeFirst();
			if (event instanceof Submit)
			{
				Submit submit = (Submit)event;
				Job job = submit.job();
				waitingQueue.add(job);
			}
			else if (event instanceof Finish)
			{
				Finish finish = (Finish)event;
				jobFinisher.finish(finish);
				
			}
			else
			{
				asserter().assertTrue(event instanceof NoOp, "no handle for event " + event);
			}
		}
		return $;
	}
	
	public void setTimeToLog(long time)
	{
		timeToLog = time;
	}
}
