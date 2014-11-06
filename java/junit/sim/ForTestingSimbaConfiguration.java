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
	public double hostCoreRatio()
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

	@Override
	public boolean isActualCoreUsageSimulation()
	{
		return false;
	}

	@Override
	public int jobsCheckedBySchduler()
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public double submitRatio()
	{
		return 1;
	}

	@Override
	public long collectTime()
	{
		return 300;
	}

	@Override
	public Double fixedMemory()
	{
		return null;
	}

	@Override
	public Double fixedCores()
	{
		return null;
	}

	@Override
	public boolean isFixedCost()
	{
		return false;
	}
}