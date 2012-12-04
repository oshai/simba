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
	private final SimbaConsts simbaConsts;

	public SimbaModule(SimbaConsts simbaConsts)
	{
		super();
		this.simbaConsts = simbaConsts;
	}

	@Override
	protected void configure()
	{
		bind(SimbaConsts.class).toInstance(simbaConsts);
		install(new FactoryModuleBuilder().implement(Looper.class, Looper.class).build(LooperFactory.class));
	}

	public interface LooperFactory
	{
		Looper create(Clock clock, EventQueue eventQueue, AbstractWaitingQueue waitingQueue, Scheduler scheduler, IntervalCollector hostCollector,
				JobFinisher jobFinisher);
	}

}
