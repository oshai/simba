package sim;

public interface SimbaConfiguration
{
	public boolean isBucketSimulation();

	public long bucketSize();

	public long timeToSchedule();

	public long timeToLog();

	double hostCoreRatio();

	double hostMemoryRatio();

	public int reservationsLimit();

	double machineDropRatio();

	public boolean isActualCoreUsageSimulation();

	public int jobsCheckedBySchduler();

	double submitRatio();

	public long collectTime();

	public Double fixedMemory();

	public Double fixedCores();

}
