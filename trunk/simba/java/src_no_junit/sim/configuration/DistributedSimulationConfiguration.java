package sim.configuration;

import sim.DistributedSimbaConfiguration;

public class DistributedSimulationConfiguration extends ProductionSimbaConfiguration implements DistributedSimbaConfiguration
{

	public double virusPower = 10;
	public long virusTime = 10;

	@Override
	public double virusPower()
	{
		return virusPower;
	}

	@Override
	public long virusTime()
	{
		return virusTime;
	}

}
