package sim.collectors;

import java.util.List;

public class CompositeCollector implements IntervalCollector
{

	private List<IntervalCollector> collectors;

	public CompositeCollector(List<IntervalCollector> collectors)
	{
		super();
		this.collectors = collectors;
	}

	@Override
	public void collect(long time, boolean handeledEvents, int scheduledJobs)
	{
		for (IntervalCollector c : collectors)
		{
			c.collect(time, handeledEvents, scheduledJobs);
		}
	}

	@Override
	public void finish()
	{
		for (IntervalCollector c : collectors)
		{
			c.finish();
		}
	}

}
