package sim;

public class ProductionSimbaConsts implements SimbaConsts
{
	private boolean bucketSimulation = false;
	private int bucketSize = 10800;
	private long timeToLog = 60 * 60 * 24;// 1 day
	private int timeToSchedule = 10;

	public boolean isBucketSimulation()
	{
		return bucketSimulation;
	}

	@Override
	public long bucketSize()
	{
		return bucketSize;
	}

	@Override
	public long timeToSchedule()
	{
		return timeToSchedule;
	}

	@Override
	public long timeToLog()
	{
		return timeToLog;
	}
}
