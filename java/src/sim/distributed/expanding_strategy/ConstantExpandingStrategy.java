package sim.distributed.expanding_strategy;

import sim.DistributedSimbaConfiguration;

public class ConstantExpandingStrategy implements ExpandingStrategy
{
	private final DistributedSimbaConfiguration simbaConfiguration;

	public ConstantExpandingStrategy(DistributedSimbaConfiguration simbaConfiguration)
	{
		this.simbaConfiguration = simbaConfiguration;
	}

	@Override
	public int times(long iteration, double memory)
	{
		return simbaConfiguration.intialDispatchFactor();
	}

}
