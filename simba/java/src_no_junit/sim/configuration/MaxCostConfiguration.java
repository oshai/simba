package sim.configuration;

public class MaxCostConfiguration extends MaxJobsConfiguration
{
	@Override
	public boolean isFixedCost()
	{
		return false;
	}
}
