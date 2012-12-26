package sim.configuration;

import sim.Clock;
import sim.JobFinisher;
import sim.Looper;
import sim.SimbaConfiguration;
import sim.collectors.IntervalCollector;
import sim.event_handling.EventQueue;
import sim.model.Cluster;
import sim.parsers.HostParser;
import sim.parsers.JobParser;
import sim.scheduling.Scheduler;
import sim.scheduling.waiting_queue.AbstractWaitingQueue;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ProductionSimbaConfiguration extends AbstractModule implements Module, SimbaConfiguration
{
	private final double memoryRatio = Double.valueOf(System.getProperty("host-memory-multiplier", "1.0"));
	private final double coreRatio = Double.valueOf(System.getProperty("cores-ratio", "1.0"));
	private final double machineDropRatio = Double.valueOf(System.getProperty("machine-drop-ratio", "1.0"));
	private final double submitRatio = Double.valueOf(System.getProperty("submit-ratio", "1.0"));
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
		bind(HostParser.class).in(Scopes.SINGLETON);
		bind(Clock.class).in(Scopes.SINGLETON);
		bind(JobParser.class).in(Scopes.SINGLETON);
		bind(EventQueue.class).in(Scopes.SINGLETON);
		bind(Cluster.class).in(Scopes.SINGLETON);
		install(new FactoryModuleBuilder().implement(Looper.class, Looper.class).build(LooperFactory.class));
	}

	public interface LooperFactory
	{
		Looper create(Clock clock, EventQueue eventQueue, AbstractWaitingQueue waitingQueue, Scheduler scheduler, IntervalCollector hostCollector, JobFinisher jobFinisher);
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
	public double hostCoreRatio()
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
	public double submitRatio()
	{
		return submitRatio;
	}

	public boolean isBucketSimulation()
	{
		return false;
	}

	@Override
	public long bucketSize()
	{
		return bucketSize;
	}

	@Override
	public String toString()
	{
		return "ProductionSimbaConfiguration [timeToSchedule()=" + timeToSchedule() + ", timeToLog()=" + timeToLog() + ", hostCoreRatio()=" + hostCoreRatio() + ", machineDropRatio()=" + machineDropRatio() + ", hostMemoryRatio()=" + hostMemoryRatio() + ", reservationsLimit()=" + reservationsLimit() + ", isActualCoreUsageSimulation()=" + isActualCoreUsageSimulation() + ", jobsCheckedBySchduler()=" + jobsCheckedBySchduler() + ", submitRatio()=" + submitRatio() + ", isBucketSimulation()=" + isBucketSimulation() + ", bucketSize()=" + bucketSize() + "]";
	}

}
