package sim;

import sim.configuration.ProductionSimbaConfiguration;

public class SemiProductionSimbaConfiguration extends ProductionSimbaConfiguration
{
	@Override
	public long timeToSchedule()
	{
		return 1;
	}

}