package sim.scheduling.matchers;

import java.util.List;

import sim.model.Host;
import sim.model.Job;

public class BestFit implements Matcher
{

	@Override
	public Host match(Job job, List<Host> hosts)
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
				else if (host.availableMemory() < $.availableMemory())
				{
					$ = host;
				}
			}
		}
		return $;
	}

}
