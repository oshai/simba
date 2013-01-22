package sim.configuration;

import java.util.List;

import sim.ParallelScheduleCalculator;
import sim.collectors.MaxCostCollector;
import sim.scheduling.Scheduler;
import sim.scheduling.reserving.IMaxCostCollector;
import sim.scheduling.reserving.MaxCostScheduler;
import sim.scheduling.reserving.ReservingScheduler;
import sim.scheduling.reserving.ScheduleCalculator;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

public class MaxCostConfiguration extends ProductionSimbaConfiguration
{
	@Override
	public void configure()
	{
		super.configure();
		bindSchedulerList();
		bind(ScheduleCalculator.class).to(ParallelScheduleCalculator.class);
	}

	protected void bindSchedulerList()
	{
		bind(new TypeLiteral<List<ReservingScheduler>>()
		{
		}).toProvider(SchedulersProvider.class);
	}

	@Override
	protected void bindMaxCostCollector()
	{
		bind(IMaxCostCollector.class).to(MaxCostCollector.class).in(Scopes.SINGLETON);
	}

	@Override
	protected Class<? extends Scheduler> scheduler()
	{
		return MaxCostScheduler.class;
	}
}
