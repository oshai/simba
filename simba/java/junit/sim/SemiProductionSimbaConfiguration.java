package sim;

public class SemiProductionSimbaConfiguration extends ProductionSimbaConfiguration
{
	@Override
	public long timeToSchedule()
	{
		return 1;
	}

}