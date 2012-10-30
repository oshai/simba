package sim;

import sim.collectors.JobCollector;
import sim.events.Finish;
import sim.model.Host;
import sim.model.HostsHolder;

public class JobFinisher
{
	private final JobCollector jobCollector;
	private final HostsHolder hostsHolder;
	
	public JobFinisher(JobCollector jobCollector, HostsHolder hostsHolder)
	{
		super();
		this.jobCollector = jobCollector;
		this.hostsHolder = hostsHolder;
	}
	
	public void finish(Finish finish)
	{
		Host host = finish.host();
		hostsHolder.beforeUpdate(host);
		host.finishJob(finish.job());
		hostsHolder.afterUpdate(host);
		jobCollector.collect(finish.job());
	}
	
	public void finishExecution()
	{
		jobCollector.finish();
	}
}
