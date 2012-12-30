package sim.configuration;

public class BucketSimulationConfiguration extends ProductionSimbaConfiguration
{
	@Override
	public boolean isBucketSimulation()
	{
		return true;
	}

	@Override
	public long collectTime()
	{
		return bucketSize();
	}
}
