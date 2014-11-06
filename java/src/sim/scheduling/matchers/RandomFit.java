package sim.scheduling.matchers;

import java.util.List;
import java.util.Random;

import sim.model.Host;
import sim.model.Job;

public class RandomFit implements Matcher
{
	private final Random random;

	public RandomFit()
	{
		this(new Random());
	}

	public RandomFit(Random random)
	{
		super();
		this.random = random;
	}

	@Override
	public Host match(Job job, List<Host> hosts)
	{
		int i = random.nextInt(hosts.size());
		for (int j = i; j < hosts.size(); j++)
		{
			Host host = hosts.get(j);
			if (host.hasAvailableResourcesFor(job))
			{
				return host;
			}

		}
		for (int j = 0; j < i; j++)
		{
			Host host = hosts.get(j);
			if (host.hasAvailableResourcesFor(job))
			{
				return host;
			}

		}
		return null;
	}

}
