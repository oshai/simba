package sim.distributed.expanding_strategy;

import sim.DistributedSimbaConfiguration;

public class JobSizeExpandingStrategy implements ExpandingStrategy
{
	private final DistributedSimbaConfiguration simbaConfiguration;

	public JobSizeExpandingStrategy(DistributedSimbaConfiguration simbaConfiguration)
	{
		super();
		this.simbaConfiguration = simbaConfiguration;
	}

	@Override
	public int times(long iteration, double memory)
	{
		return (int) memory / simbaConfiguration.intialDispatchFactor() + 1;
	}

}
