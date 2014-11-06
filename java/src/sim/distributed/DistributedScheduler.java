package sim.distributed;

import static com.google.common.collect.Lists.*;
import static utils.assertions.Asserter.*;

import java.util.Iterator;
import java.util.List;

import sim.DistributedSimbaConfiguration;
import sim.collectors.CostStatistics;
import sim.distributed.expanding_strategy.ExpandingStrategy;
import sim.model.Job;
import sim.scheduling.Scheduler;
import sim.scheduling.waiting_queue.AbstractWaitingQueue;
import sim.scheduling.waiting_queue.SetWaitingQueue;

public class DistributedScheduler implements Scheduler
{
	private final AbstractWaitingQueue waitingQueue;
	private final List<HostScheduler> hostSchedulers;
	private SetWaitingQueue distributedWaitingJobs;
	private DistributedSchedulerLogger schedulerLogger;
	private final HostSelector hostSelector;
	private final DistributedSimbaConfiguration simbaConfiguration;
	private ExpandingStrategy expandingStrategy;
	private final CostStatistics costStatistics;

	public DistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector, SetWaitingQueue distributedWaitingJobs, DistributedSimbaConfiguration simbaConfiguration, ExpandingStrategy expandingStrategy, CostStatistics costStatistics)
	{
		this.waitingQueue = waitingQueue;
		this.hostSchedulers = hostSchedulers;
		this.distributedWaitingJobs = distributedWaitingJobs;
		this.costStatistics = costStatistics;
		schedulerLogger = new DistributedSchedulerLogger(waitingQueue, hostSchedulers, distributedWaitingJobs);
		this.hostSelector = hostSelector;
		this.simbaConfiguration = simbaConfiguration;
		this.expandingStrategy = expandingStrategy;
	}

	@Override
	public int schedule(long time)
	{
		long started = System.currentTimeMillis();
		long newJobs = waitingQueue.size();
		int waitingJobs = distributeJobs(time);
		costStatistics.calculate();
		int dispatchJobs = dispatch(time);
		schedulerLogger.log(time, started, newJobs, waitingJobs, dispatchJobs);
		asserter().throwsError().assertFalse(waitingQueue.size() > 0, "waiting queue should always be zero in the end of cycle first waiting job: " + waitingQueue.peek() + " is already waiting on hosts? " + distributedWaitingJobs.contains(waitingQueue.peek()));
		return dispatchJobs;
	}

	private final SetWaitingQueue distributedWaitingJobs()
	{
		return distributedWaitingJobs;
	}

	private final AbstractWaitingQueue waitingQueue()
	{
		return waitingQueue;
	}

	private final List<HostScheduler> hostSchedulers()
	{
		return hostSchedulers;
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

	private void addJobToHost(Job j, HostScheduler h)
	{
		h.addJob(j);
		distributedWaitingJobs.add(j);
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