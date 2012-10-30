package sim.scheduling.matchers;

import sim.model.Host;
import sim.model.Job;

public class BestFit implements Matcher
{

	@Override
	public Host match(Job job, Iterable<Host> hosts)
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
