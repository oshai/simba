package sim.scheduling.mix_fit;

import java.util.List;

import sim.model.Host;
import sim.model.Job;
import utils.GlobalUtils;

public class HostPicker
{
	
	private final List<Host> hosts;
	
	public HostPicker(List<Host> hosts)
	{
		this.hosts = hosts;
	}
	
	public Host getBestHost(Job job)
	{
		Host $ = null;
		for (Host host : hosts)
		{
			if (host.hasAvailableResourcesFor(job))
			{
				if (null == $)
				{
					$ = host;
				}
				else if (getRating(host, job) < getRating($, job))
				{
					$ = host;
				}
				else if (GlobalUtils.equals(getRating(host, job), getRating($, job)))
				{
					if (host.availableMemory() < $.availableMemory())
					{
						$ = host;
					}
				}
			}
		}
		return $;
	}
	
	private double getRating(Host host, Job job)
	{
		double hostRatio = host.memory() / host.cores();
		double usageRatio = (host.usedMemory() + job.memory()) / (host.usedCores() + job.cores());
		return Math.abs(Math.atan(hostRatio) - Math.atan(usageRatio));
	}
	
}
