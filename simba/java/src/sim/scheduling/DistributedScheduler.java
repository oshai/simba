package sim.scheduling;

import java.util.Iterator;
import java.util.List;

import sim.model.Job;

public class DistributedScheduler implements Scheduler
{
	private final AbstractWaitingQueue waitingQueue;
	private final List<HostScheduler> hostSchedulers;
	private final HostSelector hostSelector;

	public DistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector)
	{
		super();
		this.waitingQueue = waitingQueue;
		this.hostSchedulers = hostSchedulers;
		this.hostSelector = hostSelector;
	}

	@Override
	public int schedule(long time)
	{
		distributeJobs();
		return dispatch(time);
	}

	private int dispatch(long time)
	{
		int $ = 0;
		for (HostScheduler h : hostSchedulers)
		{
			$ += h.schedule(time);
		}
		return $;
	}

	private void distributeJobs()
	{
		for (Iterator<Job> iterator = waitingQueue.iterator(); iterator.hasNext();)
		{
			Job j = iterator.next();
			HostScheduler hostScheduler = hostSelector.select(j);
			if (null != hostScheduler)
			{
				iterator.remove();
				hostScheduler.addJob(j);
			}
		}
	}

}
