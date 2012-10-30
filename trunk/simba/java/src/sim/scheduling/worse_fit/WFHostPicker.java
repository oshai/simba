package sim.scheduling.worse_fit;

import java.util.List;

import sim.model.Host;
import sim.model.Job;

public class WFHostPicker
{
	
	private final List<Host> hosts;
	
	public WFHostPicker(List<Host> hosts)
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
				else if (host.availableMemory() > $.availableMemory())
				{
					$ = host;
				}
			}
		}
		return $;
	}
	
}
