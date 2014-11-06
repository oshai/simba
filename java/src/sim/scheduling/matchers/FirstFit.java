package sim.scheduling.matchers;

import java.util.List;

import sim.model.Host;
import sim.model.Job;

public class FirstFit implements Matcher
{
	@Override
	public Host match(Job job, List<Host> list)
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
