package sim.distributed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.JobDispatcher;
import sim.scheduling.SetWaitingQueue;
import sim.scheduling.WaitingQueueForStatistics;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class HostScheduler
{
	private final Host host;
	private final AbstractWaitingQueue waitingJobs;
	private Set<Job> jobs;
	private final JobDispatcher dispatcher;
	private final SetWaitingQueue distributedWaitingJobs;
	private Comparator<? super Job> jobsGrader;

	public HostScheduler(Host host, JobDispatcher dispatcher, AbstractWaitingQueue waitingQueue, SetWaitingQueue distributedWaitingJobs, Comparator<? super Job> jobGrader)
	{
		super();
		this.host = host;
		this.dispatcher = dispatcher;
		this.waitingJobs = waitingQueue;
		this.distributedWaitingJobs = distributedWaitingJobs;
		jobs = Sets.newHashSet(waitingQueue);
		jobsGrader = jobGrader;
	}

	public void didid()
	{
		// TODO Auto-generated method stub
		System.out.println();
	}

	public void addJob(Job job)
	{
		jobs.add(job);
		waitingJobs.add(job);
	}

	public int schedule(long time)
	{

		int $ = 0;
		ArrayList<Job> newArrayList = Lists.newArrayList(waitingJobs);
		Collections.sort(newArrayList, jobsGrader);
		Iterator<Job> iterator = newArrayList.iterator();
		while (iterator.hasNext())
		{
			Job job = iterator.next();
			if (!host.hasPotentialResourceFor(job) || !distributedWaitingJobs.contains(job))
			{
				removeJob(job);
				continue;
			}
			if (host.availableCores() >= job.cores() && host.availableMemory() >= job.memory())
			{
				dispatcher.dispatch(job, host, time);
				removeJob(job);
				$++;
			}
		}
		return $;
	}

	private void removeJob(Job job)
	{
		jobs.remove(job);
		waitingJobs.getQueue().remove(job);
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

	public Host host()
	{
		return host;
	}
}
