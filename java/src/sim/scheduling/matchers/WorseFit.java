package sim.scheduling.matchers;

import java.util.List;

import sim.model.Host;
import sim.model.Job;
import utils.GlobalUtils;

public class WorseFit implements Matcher
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
				else if (!GlobalUtils.equals(host.availableMemory(), $.availableMemory()) && host.availableMemory() > $.availableMemory())
				{
					$ = host;
				}
			}
		}
		return $;
	}

}
