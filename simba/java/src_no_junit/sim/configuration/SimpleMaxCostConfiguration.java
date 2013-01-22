package sim.configuration;

import java.util.List;

import sim.scheduling.reserving.ReservingScheduler;

import com.google.inject.TypeLiteral;

public class SimpleMaxCostConfiguration extends MaxCostConfiguration
{
	@Override
	protected void bindSchedulerList()
	{
		bind(new TypeLiteral<List<ReservingScheduler>>()
		{
		}).toProvider(SimpleSchedulersProvider.class);
	}
}
