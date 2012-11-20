package sim.scheduling;

import sim.event_handling.EventQueue;
import sim.events.Finish;
import sim.model.Host;
import sim.model.Job;

public class JobDispatcher
{
	
	private final EventQueue eventQueue;
	
	public JobDispatcher(EventQueue eventQueue)
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
