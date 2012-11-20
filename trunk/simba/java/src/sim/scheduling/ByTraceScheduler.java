package sim.scheduling;

import java.util.Iterator;

import sim.model.Host;
import sim.model.Job;

public class ByTraceScheduler implements Scheduler
{
	private static final Host DUMMY_HOST = Host.create().build();
	private final WaitingQueue waitingQueue;
	private final JobDispatcher dispatcher;

	public ByTraceScheduler(WaitingQueue waitingQueue, JobDispatcher dispatcher)
	{
		this.waitingQueue = waitingQueue;
		this.dispatcher = dispatcher;
	}

	@Override
	public void schedule(long time)
	{
		Iterator<Job> iterator = waitingQueue.iterator();
		while (iterator.hasNext())
		{
			Job job = iterator.next();
			if (job.startTime() <= time)
			{
				iterator.remove();
				dispatcher.dispatch(job, DUMMY_HOST, time);
			}
		}
	}
}
