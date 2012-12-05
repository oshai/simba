package sim;

import javax.inject.Inject;

import sim.collectors.IntervalCollector;
import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.events.Submit;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.Scheduler;
import sim.scheduling.reserving.ReservingScheduler;

import com.google.inject.assistedinject.Assisted;

public class CentralizedLooper extends Looper
{
	private AbstractWaitingQueue waitingQueue;
	private final Scheduler scheduler;

	@Inject
	public CentralizedLooper(@Assisted Clock clock, @Assisted EventQueue eventQueue, @Assisted AbstractWaitingQueue waitingQueue,
			@Assisted Scheduler scheduler, @Assisted IntervalCollector hostCollector, @Assisted JobFinisher jobFinisher, SimbaConfiguration simbaConsts)
	{
		super(clock, eventQueue, hostCollector, jobFinisher, simbaConsts);
		this.waitingQueue = waitingQueue;
		this.scheduler = scheduler;
	}

	@Override
	protected int schedule(long time)
	{
		int scheduledJobs;
		scheduledJobs = scheduler.schedule(time);
		return scheduledJobs;
	}

	@Override
	protected boolean submitJob(Event event)
	{
		boolean $ = false;
		Submit submit = (Submit) event;
		Job job = submit.job();
		waitingQueue.add(job);
		if (!(sizeOfWaitingQueue() > ReservingScheduler.JOBS_CHECKED_BY_SCHEDULER))
		{
			$ = true;
		}
		return $;
	}

	@Override
	protected int sizeOfWaitingQueue()
	{
		return waitingQueue.size();
	}

}
