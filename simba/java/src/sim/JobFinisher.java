package sim;

import org.apache.commons.math3.util.Pair;

import sim.collectors.JobCollector;
import sim.events.Finish;
import sim.model.Host;
import sim.model.Job;

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
		jobCollector.collect(new Pair<Job, Host>(finish.job(), finish.host()));
	}

	public void finishExecution()
	{
		jobCollector.finish();
	}
}
