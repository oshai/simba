package sim;

import sim.collectors.JobCollector;
import sim.events.Finish;
import sim.model.Host;

public class JobFinisher
{
	private final JobCollector jobCollector;

	public JobFinisher(JobCollector jobCollector)
	{
		super();
		this.jobCollector = jobCollector;
	}

	public void finish(Finish finish)
	{
		Host host = finish.host();
		host.finishJob(finish.job());
		jobCollector.collect(finish.job());
	}

	public void finishExecution()
	{
		jobCollector.finish();
	}
}
