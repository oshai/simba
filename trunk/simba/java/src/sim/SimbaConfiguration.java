package sim;

public interface SimbaConfiguration
{
	public boolean isBucketSimulation();

	public long bucketSize();

	public long timeToSchedule();

	public long timeToLog();

	double jobCoresRatio();

	double hostMemoryRatio();

	public int reservationsLimit();
}
