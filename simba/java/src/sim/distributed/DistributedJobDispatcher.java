package sim.distributed;

import sim.event_handling.*;
import sim.model.*;
import sim.scheduling.*;

public class DistributedJobDispatcher extends JobDispatcher
{

	private final SetWaitingQueue distributedWaitingJobs;

	public DistributedJobDispatcher(EventQueue eventQueue, SetWaitingQueue distributedWaitingJobs)
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
