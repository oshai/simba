package sim.scheduling;

import java.util.Iterator;

import sim.model.Job;

public class ByTraceScheduler implements Scheduler
{
	private final WaitingQueue waitingQueue;
	private final Dispatcher dispatcher;

	public ByTraceScheduler(WaitingQueue waitingQueue, Dispatcher dispatcher)
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
				dispatcher.dipatch(job, null, time);
			}
		}
	}
}
