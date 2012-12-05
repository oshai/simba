package sim;

public class ForTestingSimbaConfiguration implements SimbaConfiguration
{
	@Override
	public boolean isBucketSimulation()
	{
		return false;
	}

	@Override
	public long bucketSize()
	{
		return 1;
	}

	@Override
	public long timeToSchedule()
	{
		return 1;
	}

	@Override
	public long timeToLog()
	{
		return 1;
	}

	@Override
	public double jobCoresRatio()
	{
		return 1.0;
	}

	@Override
	public double hostMemoryRatio()
	{
		return 1.0;
	}

	@Override
	public int reservationsLimit()
	{
		return 1;
	}

	@Override
	public double machineDropRatio()
	{
		return 1.0;
	}
}