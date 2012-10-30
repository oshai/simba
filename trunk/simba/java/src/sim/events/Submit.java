package sim.events;

import sim.model.Job;

public class Submit extends Event
{

	public Submit(Job job)
	{
		super(job.submitTime(), job);
	}

}
