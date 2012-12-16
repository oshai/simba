package sim.distributed;

import java.util.Iterator;
import java.util.List;

import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.SetWaitingQueue;

public class ExpandingDistributedScheduler extends DistributedScheduler
{
	private final HostSelector hostSelector;
	public static double VIRUS_POWER = 10;
	public static long VIRUS_TIME = 10;

	public ExpandingDistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector, SetWaitingQueue distributedWaitingJobs)
	{
		super(waitingQueue, hostSchedulers, distributedWaitingJobs);
		this.hostSelector = hostSelector;
	}

	@Override
	protected void scheduleWaitingJobsAgain(long time)
	{
		for (Job job : distributedWaitingJobs())
		{
			long waitingTime = time - job.submitTime();
			if (waitingTime % ExpandingDistributedScheduler.VIRUS_TIME == 0)
			{
				for (int i = 0; i < hostSchedulers().size() && i < Math.pow(ExpandingDistributedScheduler.VIRUS_POWER, (waitingTime / ExpandingDistributedScheduler.VIRUS_TIME) - 1); i++)
				{
					waitingQueue().add(job);
				}
			}
		}
	}

	@Override
	protected void distributeJobs(long time)
	{
		for (Iterator<Job> iterator = waitingQueue().iterator(); iterator.hasNext();)
		{
			Job j = iterator.next();
			HostScheduler hostScheduler = hostSelector.select(j);
			if (null != hostScheduler)
			{
				iterator.remove();
				addJobToHost(j, hostScheduler);
			}
			else if (distributedWaitingJobs().contains(j))
			{
				iterator.remove();
			}
		}
	}

}
