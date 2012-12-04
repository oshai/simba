package sim.collectors;

import sim.model.Host;
import sim.model.Job;

public class JobCollector extends Collector
{

	private static final String JOBS_FILE_NAME = "jobs_trace";

	@Override
	protected String collectHeader()
	{
		return "#id" + SEPERATOR + "submitTime" + SEPERATOR + "waitTime" + SEPERATOR + "length" + SEPERATOR + "cores" + SEPERATOR + "memory" + SEPERATOR
				+ "priority" + SEPERATOR + "host";
	}

	public void collect(Host host, Job job)
	{
		appendLine(collectLine(host, job));
	}

	private String collectLine(Host host, Job job)
	{
		return job.id() + SEPERATOR + job.submitTime() + SEPERATOR + job.waitTime() + SEPERATOR + job.length() + SEPERATOR + job.cores() + SEPERATOR
				+ job.memory() + SEPERATOR + job.priority() + SEPERATOR + host.id();
	}

	@Override
	protected String getFileName()
	{
		return JOBS_FILE_NAME;
	}

}
