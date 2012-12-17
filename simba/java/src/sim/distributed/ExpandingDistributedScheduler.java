package sim.distributed;

import java.util.Iterator;
import java.util.List;

import sim.DistributedSimbaConfiguration;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.SetWaitingQueue;

public class ExpandingDistributedScheduler extends DistributedScheduler
{
	private final HostSelector hostSelector;
	private final DistributedSimbaConfiguration simbaConfiguration;

	public ExpandingDistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector, SetWaitingQueue distributedWaitingJobs, DistributedSimbaConfiguration simbaConfiguration)
	{
		super(waitingQueue, hostSchedulers, distributedWaitingJobs);
		this.hostSelector = hostSelector;
		this.simbaConfiguration = simbaConfiguration;
	}

	@Override
	protected void scheduleWaitingJobsAgain(long time)
	{
		for (Job job : distributedWaitingJobs())
		{
			long waitingTime = time - job.submitTime();
			if (isTimeToDivide(waitingTime) && isLastIterationFilledAllHosts(waitingTime))
			{
				for (int i = 0; i < hostSchedulers().size() && i < virusDivisionFactor(waitingTime, 1); i++)
				{
					waitingQueue().add(job);
				}
			}
		}
	}

	private boolean isTimeToDivide(long waitingTime)
	{
		return waitingTime % simbaConfiguration.virusTime() < simbaConfiguration.timeToSchedule();
	}

	private boolean isLastIterationFilledAllHosts(long waitingTime)
	{
		return virusDivisionFactor(waitingTime, 2) < hostSchedulers().size();
	}

	private double virusDivisionFactor(long waitingTime, int factor)
	{
		return Math.pow(simbaConfiguration.virusPower(), (waitingTime / simbaConfiguration.virusTime()) - factor);
	}

	@Override
	protected int distributeJobs(long time)
	{
		int $ = waitingQueue().size();
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
		return $;
	}

}
