package sim.distributed;

import java.util.Iterator;
import java.util.List;

import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.SetWaitingQueue;

public class WaitOnAllHostsDistributedScheduler extends DistributedScheduler
{

	public WaitOnAllHostsDistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, SetWaitingQueue distributedWaitingJobs)
	{
		super(waitingQueue, hostSchedulers, distributedWaitingJobs);
	}

	@Override
	protected final void distributeJobs(long time)
	{
		for (Iterator<Job> iterator = waitingQueue().iterator(); iterator.hasNext();)
		{
			Job j = iterator.next();
			for (HostScheduler h : hostSchedulers())
			{
				addJobToHost(j, h);
			}
			iterator.remove();

		}
	}

	@Override
	protected final void scheduleWaitingJobsAgain(long time)
	{
		// do nothing
	}

}
