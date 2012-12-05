package sim;

import sim.collectors.IntervalCollector;
import sim.event_handling.EventQueue;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.Scheduler;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SimbaModule extends AbstractModule implements Module
{
	private final SimbaConfiguration simbaConsts;

	public SimbaModule(SimbaConfiguration simbaConsts)
	{
		super();
		this.simbaConsts = simbaConsts;
	}

	@Override
	protected void configure()
	{
		bind(SimbaConfiguration.class).toInstance(simbaConsts);
		install(new FactoryModuleBuilder().implement(CentralizedLooper.class, CentralizedLooper.class).build(LooperFactory.class));
	}

	public interface LooperFactory
	{
		CentralizedLooper create(Clock clock, EventQueue eventQueue, AbstractWaitingQueue waitingQueue, Scheduler scheduler, IntervalCollector hostCollector,
				JobFinisher jobFinisher);
	}

}
