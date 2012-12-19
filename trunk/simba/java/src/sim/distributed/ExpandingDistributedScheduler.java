package sim.distributed;

import static com.google.common.collect.Lists.*;

import java.util.Iterator;
import java.util.List;

import sim.DistributedSimbaConfiguration;
import sim.distributed.expanding_strategy.ExpandingStrategy;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.SetWaitingQueue;

public class ExpandingDistributedScheduler extends DistributedScheduler
{
	private final HostSelector hostSelector;
	private final DistributedSimbaConfiguration simbaConfiguration;
	private ExpandingStrategy expandingStrategy;

	public ExpandingDistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector, SetWaitingQueue distributedWaitingJobs, DistributedSimbaConfiguration simbaConfiguration, ExpandingStrategy expandingStrategy)
	{
		super(waitingQueue, hostSchedulers, distributedWaitingJobs);
		this.hostSelector = hostSelector;
		this.simbaConfiguration = simbaConfiguration;
		this.expandingStrategy = expandingStrategy;
	}

	private void scheduleWaitingJobsAgain(long time)
	{
		for (Job job : distributedWaitingJobs())
		{
			long waitingTime = time - job.submitTime();
			if (isTimeToDivide(waitingTime) && isLastIterationFilledAllHosts(waitingTime, job.memory()))
			{
				for (int i = 0; i < hostSchedulers().size() && i < expandingStrategy.times((waitingTime / simbaConfiguration.virusTime()) - 1, job.memory()); i++)
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

	private boolean isLastIterationFilledAllHosts(long waitingTime, double memory)
	{
		return expandingStrategy.times((waitingTime / simbaConfiguration.virusTime()) - 1, memory) < hostSchedulers().size();
	}

	@Override
	protected int distributeJobs(long time)
	{
		duplicateWaitingJobs();
		scheduleWaitingJobsAgain(time);
		int $ = waitingQueue().size();
		assignHosts();
		return $;
	}

	private void assignHosts()
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

	private void duplicateWaitingJobs()
	{
		List<Job> jobs = newArrayList(waitingQueue());
		for (Job j : jobs)
		{
			for (int i = 1; i < simbaConfiguration.intialDispatchFactor(); i++)
			{
				waitingQueue().add(j);
			}
		}
	}

}
