package sim.distributed.expanding_strategy;

import sim.DistributedSimbaConfiguration;

public class PowerExpandingStrategy implements ExpandingStrategy
{
	private DistributedSimbaConfiguration simbaConfiguration;

	public PowerExpandingStrategy(DistributedSimbaConfiguration simbaConfiguration)
	{
		this.simbaConfiguration = simbaConfiguration;
	}

	@Override
	public int times(long iteration, double memory)
	{
		return (int) Math.pow(simbaConfiguration.virusPower(), iteration);
	}
}