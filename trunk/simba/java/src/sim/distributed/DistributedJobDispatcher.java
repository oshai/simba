package sim.distributed;

import java.util.Set;

import sim.event_handling.EventQueue;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.JobDispatcher;

public class DistributedJobDispatcher extends JobDispatcher
{

	private final Set<Job> distributedWaitingJobs;

	public DistributedJobDispatcher(EventQueue eventQueue, Set<Job> distributedWaitingJobs)
	{
		super(eventQueue);
		this.distributedWaitingJobs = distributedWaitingJobs;
	}

	@Override
	public void dispatch(Job job, Host host, long currentTime)
	{

		if (distributedWaitingJobs.remove(job))
		{
			super.dispatch(job, host, currentTime);
		}
	}
}
