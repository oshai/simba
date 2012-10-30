package sim.collectors;

import sim.model.Job;

public class JobCollector extends Collector<Job>
{
	
	private static final String JOBS_FILE_NAME = "jobs_trace";
	
	@Override
	protected String collectHeader()
	{
		return "#id" + SEPERATOR + "submitTime" + SEPERATOR + "waitTime" + SEPERATOR + "length" + SEPERATOR + "cores" + SEPERATOR + "memory";
	}
	
	@Override
	protected String collectLine(Job job)
	{
		return job.id() + SEPERATOR + job.submitTime() + SEPERATOR + job.waitTime() + SEPERATOR + job.length() + SEPERATOR + job.cores() + SEPERATOR
				+ job.memory();
	}
	
	@Override
	protected String getFileName()
	{
		return JOBS_FILE_NAME;
	}
	
}
