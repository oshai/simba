package sim;

import sim.collectors.IJobCollector;
import sim.collectors.IntervalCollector;
import sim.configuration.ProductionSimbaConfiguration;
import sim.scheduling.Scheduler;
import sim.scheduling.SimpleScheduler;
import sim.scheduling.matchers.FirstFit;
import sim.scheduling.matchers.Matcher;
import sim.scheduling.reserving.IMaxCostCollector;

public class SemiProductionSimbaConfiguration extends ProductionSimbaConfiguration
{
	@Override
	protected void configure()
	{
		super.configure();
		bind(Matcher.class).to(FirstFit.class);
	}

	@Override
	protected void bindCollectors()
	{
		bind(IntervalCollector.class).toInstance(IntervalCollector.NO_OP);
		bind(IJobCollector.class).toInstance(IJobCollector.NO_OP);
		bind(IMaxCostCollector.class).toInstance(IMaxCostCollector.NO_OP);
	}

	@Override
	public long timeToSchedule()
	{
		return 1;
	}

	@Override
	protected Class<? extends Scheduler> scheduler()
	{
		return SimpleScheduler.class;
	}
}