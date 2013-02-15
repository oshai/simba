package sim.events;

import sim.model.Job;

public class Event
{
	private final long time;
	private final Job job;

	public Event(long time, Job job)
	{
		this.time = time;
		this.job = job;
	}

	public long time()
	{
		return time;
	}

	public Job job()
	{
		return job;
	}

	@Override
	public String toString()
	{
		return "Event [time=" + time + ", job=" + job + "]";
	}

}