package sim.scheduling.matchers;

import java.util.List;

import sim.model.Host;
import sim.model.Job;
import utils.GlobalUtils;

public class ActualBestFit implements Matcher
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
				else if (host.memory() < $.memory())
				{
					$ = host;
				}
				else if (GlobalUtils.equals(host.memory(), $.memory()) && host.availableMemory() < $.availableMemory())
				{
					$ = host;
				}
			}
		}
		return $;
	}

}
