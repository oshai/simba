package sim;

import sim.collectors.IntervalCollector;
import sim.event_handling.EventQueue;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.Scheduler;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ProductionSimbaConfiguration extends AbstractModule implements Module, SimbaConfiguration
{
	private final double memoryRatio = Double.valueOf(System.getProperty("host-memory-multiplier", "1.0"));
	private final double coreRatio = Double.valueOf(System.getProperty("cores-ratio", "1.0"));
	private final double machineDropRatio = Double.valueOf(System.getProperty("machine-drop-ratio", "1.0"));
	private boolean bucketSimulation = false;
	private int bucketSize = 10800;
	private long timeToLog = 60 * 60 * 24;// 1 day
	private int timeToSchedule = 10;
	private final int reservationsLimit = Integer.valueOf(System.getProperty("reservations", "1"));
	private int jobsCheckedBySchduler = 10000;
	private boolean actualCoreUsageSimulation = Boolean.valueOf(System.getProperty("real-core", "false"));

	@Override
	protected void configure()
	{
		bind(SimbaConfiguration.class).toInstance(this);
		install(new FactoryModuleBuilder().implement(Looper.class, Looper.class).build(LooperFactory.class));
	}

	public interface LooperFactory
	{
		Looper create(Clock clock, EventQueue eventQueue, AbstractWaitingQueue waitingQueue, Scheduler scheduler, IntervalCollector hostCollector,
				JobFinisher jobFinisher);
	}

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
		return actualCoreUsageSimulation;
	}

	@Override
	public int jobsCheckedBySchduler()
	{
		return jobsCheckedBySchduler;
	}

	@Override
	public String toString()
	{
		return "ProductionSimbaConfiguration [isBucketSimulation()=" + isBucketSimulation() + ", bucketSize()=" + bucketSize() + ", timeToSchedule()="
				+ timeToSchedule() + ", timeToLog()=" + timeToLog() + ", jobCoresRatio()=" + jobCoresRatio() + ", machineDropRatio()=" + machineDropRatio()
				+ ", hostMemoryRatio()=" + hostMemoryRatio() + ", reservationsLimit()=" + reservationsLimit() + ", isActualCoreUsageSimulation()="
				+ isActualCoreUsageSimulation() + ", jobsCheckedBySchduler()=" + jobsCheckedBySchduler() + "]";
	}

}
