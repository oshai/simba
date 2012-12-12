package sim.scheduling;

import java.util.Iterator;

import sim.model.Host;
import sim.model.Job;

public class HostScheduler
{
	private final Host host;
	private final AbstractWaitingQueue waitingJobs;
	private final JobDispatcher dispatcher;

	public HostScheduler(Host host, JobDispatcher dispatcher, AbstractWaitingQueue waitingQueue)
	{
		super();
		this.host = host;
		this.dispatcher = dispatcher;
		this.waitingJobs = waitingQueue;
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
			if (host.availableCores() >= job.cores() && host.availableMemory() >= job.memory())
			{
				dispatcher.dispatch(job, host, time);
				iterator.remove();
				$++;
			}
		}
		return $;
	}

	public boolean hasPotentialResourceFor(Job job)
	{
		return host.hasPotentialResourceFor(job);
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
