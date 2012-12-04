package sim;

import static com.google.common.collect.Lists.*;

import java.util.List;

import sim.collectors.JobCollector;
import sim.events.Finish;
import sim.model.Host;
import sim.model.Job;

public class JobFinisher
{
	private final JobCollector jobCollector;
	private List<Job> finishedJobs = newArrayList();

	public JobFinisher(JobCollector jobCollector)
	{
		super();
		this.jobCollector = jobCollector;
	}

	public void finish(Finish finish)
	{
		Host host = finish.host();
		host.finishJob(finish.job());
		jobCollector.collect(finish.host(), finish.job());
		finishedJobs.add(finish.job());
	}

	public void finishExecution()
	{
		jobCollector.finish();
	}

	public int collectFinishedJobs()
	{
		int size = finishedJobs.size();
		finishedJobs = newArrayList();
		return size;
	}
}
