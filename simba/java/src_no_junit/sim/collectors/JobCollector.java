package sim.collectors;

import org.apache.commons.math3.util.Pair;

import sim.model.Host;
import sim.model.Job;

public class JobCollector extends Collector<Pair<Job, Host>>
{

	private static final String JOBS_FILE_NAME = "jobs_trace";

	@Override
	protected String collectHeader()
	{
		return "#id" + SEPERATOR + "submitTime" + SEPERATOR + "waitTime" + SEPERATOR + "length" + SEPERATOR + "cores" + SEPERATOR + "memory" + SEPERATOR
				+ "priority" + SEPERATOR + "host";
	}

	@Override
	protected String collectLine(Pair<Job, Host> pair)
	{
		Job job = pair.getKey();
		Host host = pair.getValue();
		return job.id() + SEPERATOR + job.submitTime() + SEPERATOR + job.waitTime() + SEPERATOR + job.length() + SEPERATOR + job.cores() + SEPERATOR
				+ job.memory() + SEPERATOR + job.priority() + SEPERATOR + host.id();
	}

	@Override
	protected String getFileName()
	{
		return JOBS_FILE_NAME;
	}

}
