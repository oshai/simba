package sim.scheduling;

import javax.inject.Inject;

import sim.event_handling.IEventQueue;
import sim.events.Finish;
import sim.model.Host;
import sim.model.Job;

public class JobDispatcher
{

	private final IEventQueue eventQueue;

	@Inject
	public JobDispatcher(IEventQueue eventQueue)
	{
		this.eventQueue = eventQueue;
	}

	public void dispatch(Job job, Host host, long currentTime)
	{
		host.dispatchJob(job);
		eventQueue.add(new Finish(currentTime + job.length(), job, host));
		job.started(currentTime);
	}

}
