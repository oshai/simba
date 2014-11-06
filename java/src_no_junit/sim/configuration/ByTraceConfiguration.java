package sim.configuration;

import sim.scheduling.ByTraceScheduler;
import sim.scheduling.Scheduler;

public class ByTraceConfiguration extends ProductionSimbaConfiguration
{
	@Override
	protected Class<? extends Scheduler> scheduler()
	{
		return ByTraceScheduler.class;
	}
}
