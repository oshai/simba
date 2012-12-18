package sim.configuration;

import sim.DistributedSimbaConfiguration;

public class DistributedSimulationConfiguration extends ProductionSimbaConfiguration implements DistributedSimbaConfiguration
{
	private final double virusPower = Double.valueOf(System.getProperty("virus-power", "2"));
	private final long virusTime = Long.valueOf(System.getProperty("virus-time", "10"));
	private final int intialDispatchFactor = Integer.valueOf(System.getProperty("initial-dispatch-factor", "10"));

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

	@Override
	public String toString()
	{
		return "DistributedSimulationConfiguration [" +
				"virusPower()=" + virusPower() + 
				", virusTime()=" + virusTime() + 
				", intialDispatchFactor()=" + intialDispatchFactor() + 
				", timeToSchedule()=" + timeToSchedule() + 
				", timeToLog()=" + timeToLog() + 
				", hostCoreRatio()=" + hostCoreRatio() + 
				", machineDropRatio()=" + machineDropRatio() + 
				", hostMemoryRatio()=" + hostMemoryRatio() + 
				", reservationsLimit()=" + reservationsLimit() + 
				", isActualCoreUsageSimulation()=" + isActualCoreUsageSimulation() + 
				", jobsCheckedBySchduler()=" + jobsCheckedBySchduler() + 
				", submitRatio()=" + submitRatio() + 
				", isBucketSimulation()=" + isBucketSimulation() + 
				", bucketSize()=" + bucketSize() + 
				"]";
	}

	@Override
	public int intialDispatchFactor()
	{
		return intialDispatchFactor;
	}

}
