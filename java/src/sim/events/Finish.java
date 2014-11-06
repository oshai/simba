package sim.events;

import sim.model.Host;
import sim.model.Job;

public class Finish extends Event
{

	private final Host host;

	public Finish(long time, Job job, Host host)
	{
		super(time, job);
		this.host = host;
	}

	public Host host()
	{
		return host;
	}

}
