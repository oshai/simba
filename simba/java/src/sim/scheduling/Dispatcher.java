package sim.scheduling;

import sim.event_handling.EventQueue;
import sim.events.Finish;
import sim.model.Host;
import sim.model.Job;

public class Dispatcher
{
	
	private final EventQueue eventQueue;
	
	public Dispatcher(EventQueue eventQueue)
	{
		this.eventQueue = eventQueue;
	}
	
	public void dipatch(Job job, Host host, long currentTime)
	{
		host.dispatchJob(job);
		eventQueue.add(new Finish(currentTime + job.length(), job, host));
		job.started(currentTime);
	}
	
}
