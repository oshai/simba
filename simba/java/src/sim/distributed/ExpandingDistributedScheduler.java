package sim.distributed;

import java.util.*;

import sim.model.*;
import sim.scheduling.*;

public class ExpandingDistributedScheduler extends DistributedScheduler
{
	private final HostSelector hostSelector;

	public ExpandingDistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector, SetWaitingQueue distributedWaitingJobs)
	{
		super(waitingQueue, hostSchedulers, distributedWaitingJobs);
		this.hostSelector = hostSelector;
	}

	@Override
	protected void scheduleWaitingJobsAgain(long time)
	{
		for (Job job : distributedWaitingJobs)
		{
			long waitingTime = time - job.submitTime();
			if (waitingTime % VIRUS_TIME == 0)
			{
				for (int i = 0; i < hostSchedulers.size() && i < Math.pow(VIRUS_POWER, (waitingTime / VIRUS_TIME) - 1); i++)
				{
					waitingQueue.add(job);
				}
			}
		}
	}

	@Override
	protected void distributeJobs(long time)
	{
		for (Iterator<Job> iterator = waitingQueue.iterator(); iterator.hasNext();)
		{
			Job j = iterator.next();
			HostScheduler hostScheduler = hostSelector.select(j);
			if (null != hostScheduler)
			{
				iterator.remove();
				addJobToHost(j, hostScheduler);
			}
		}
	}

}
