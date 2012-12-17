package sim.distributed;

import java.util.Iterator;
import java.util.Set;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.JobDispatcher;
import sim.scheduling.SetWaitingQueue;
import sim.scheduling.WaitingQueueForStatistics;

import com.google.common.collect.Sets;

public class HostScheduler
{
	private final Host host;
	private final AbstractWaitingQueue waitingJobs;
	private Set<Job> jobs;
	private final JobDispatcher dispatcher;
	private final SetWaitingQueue distributedWaitingJobs;

	public HostScheduler(Host host, JobDispatcher dispatcher, AbstractWaitingQueue waitingQueue, SetWaitingQueue distributedWaitingJobs)
	{
		super();
		this.host = host;
		this.dispatcher = dispatcher;
		this.waitingJobs = waitingQueue;
		this.distributedWaitingJobs = distributedWaitingJobs;
		jobs = Sets.newHashSet(waitingQueue);
	}

	public void addJob(Job job)
	{
		jobs.add(job);
		waitingJobs.add(job);
	}

	public int schedule(long time)
	{

		int $ = 0;
		Iterator<Job> iterator = waitingJobs.iterator();
		while (iterator.hasNext())
		{
			Job job = iterator.next();
			if (!host.hasPotentialResourceFor(job) || !distributedWaitingJobs.contains(job))
			{
				jobs.remove(job);
				iterator.remove();
				continue;
			}
			if (host.availableCores() >= job.cores() && host.availableMemory() >= job.memory())
			{
				dispatcher.dispatch(job, host, time);
				jobs.remove(job);
				iterator.remove();
				$++;
			}
		}
		return $;
	}

	public boolean isAllowedToAddJob(Job job)
	{
		return host.hasPotentialResourceFor(job) && !(jobs.contains(job));
	}

	public int waitingJobsSize()
	{
		return waitingJobs.size();
	}

	public int collectAdd()
	{
		return waitingJobs.collectAdd();
	}

	public int collectRemove()
	{
		return waitingJobs.collectRemove();
	}

	public WaitingQueueForStatistics waitingJobs()
	{
		return waitingJobs;
	}
}
