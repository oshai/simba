package sim.scheduling.matchers;

import sim.model.Host;
import sim.model.Job;

public class FirstFit implements Matcher
{
	@Override
	public Host match(Job job, Iterable<Host> list)
	{
		for (Host host : list)
		{
			if (host.hasAvailableResourcesFor(job))
			{
				return host;
			}
		}
		return null;
	}
	
}
