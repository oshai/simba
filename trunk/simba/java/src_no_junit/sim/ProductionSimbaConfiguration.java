package sim;

public class ProductionSimbaConfiguration implements SimbaConfiguration
{
	private final double memoryRatio = Double.valueOf(System.getProperty("host-memory-multiplier", "1.0"));
	private final double coreRatio = Double.valueOf(System.getProperty("cores-ratio", "1.0"));
	private final double machineDropRatio = Double.valueOf(System.getProperty("machine-drop-ratio", "1.0"));
	private boolean bucketSimulation = false;
	private int bucketSize = 10800;
	private long timeToLog = 60 * 60 * 24;// 1 day
	private int timeToSchedule = 10;
	private final int reservationsLimit = Integer.valueOf(System.getProperty("reservations", "1"));
	private int jobsCheckedBySchduler = 5000;

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

	@Override
	public double jobCoresRatio()
	{
		return coreRatio;
	}

	@Override
	public double machineDropRatio()
	{
		return machineDropRatio;
	}

	@Override
	public double hostMemoryRatio()
	{
		return memoryRatio;
	}

	@Override
	public int reservationsLimit()
	{
		return reservationsLimit;
	}

	@Override
	public boolean isActualCoreUsageSimulation()
	{
		return false;
	}

	@Override
	public int jobsCheckedBySchduler()
	{
		return jobsCheckedBySchduler;
	}

	@Override
	public String toString()
	{
		return "ProductionSimbaConfiguration [memoryRatio=" + memoryRatio + ", coreRatio=" + coreRatio + ", machineDropRatio=" + machineDropRatio
				+ ", bucketSimulation=" + bucketSimulation + ", bucketSize=" + bucketSize + ", timeToLog=" + timeToLog + ", timeToSchedule=" + timeToSchedule
				+ ", reservationsLimit=" + reservationsLimit + ", jobsCheckedBySchduler=" + jobsCheckedBySchduler + "]";
	}

}
