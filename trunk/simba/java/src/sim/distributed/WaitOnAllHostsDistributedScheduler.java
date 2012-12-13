package sim.distributed;

import java.util.*;

import sim.model.*;
import sim.scheduling.*;

public class WaitOnAllHostsDistributedScheduler extends DistributedScheduler
{

	public WaitOnAllHostsDistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, SetWaitingQueue distributedWaitingJobs)
	{
		super(waitingQueue, hostSchedulers, distributedWaitingJobs);
	}

	@Override
	protected void distributeJobs(long time)
	{
		for (Iterator<Job> iterator = waitingQueue.iterator(); iterator.hasNext();)
		{
			Job j = (Job) iterator.next();
			for (HostScheduler h : hostSchedulers)
			{
				addJobToHost(j, h);
			}
			iterator.remove();

		}
	}

	@Override
	protected void scheduleWaitingJobsAgain(long time)
	{
		// do nothing

	}

}
