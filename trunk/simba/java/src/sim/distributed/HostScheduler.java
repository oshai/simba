package sim.distributed;

import java.util.Iterator;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.JobDispatcher;
import sim.scheduling.SetWaitingQueue;

public class HostScheduler
{
	private final Host host;
	private final AbstractWaitingQueue waitingJobs;
	private final JobDispatcher dispatcher;
	private final SetWaitingQueue distributedWaitingJobs;

	public HostScheduler(Host host, JobDispatcher dispatcher, AbstractWaitingQueue waitingQueue, SetWaitingQueue distributedWaitingJobs)
	{
		super();
		this.host = host;
		this.dispatcher = dispatcher;
		this.waitingJobs = waitingQueue;
		this.distributedWaitingJobs = distributedWaitingJobs;
	}

	public void addJob(Job job)
	{
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
				iterator.remove();
				continue;
			}
			if (host.availableCores() >= job.cores() && host.availableMemory() >= job.memory())
			{
				dispatcher.dispatch(job, host, time);
				iterator.remove();
				$++;
			}
		}
		return $;
	}

	public boolean isAllowedToAddJob(Job job)
	{
		return host.hasPotentialResourceFor(job) && !(waitingJobs.contains(job));
	}

	public int waitingJobs()
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
}
