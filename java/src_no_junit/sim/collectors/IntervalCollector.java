package sim.collectors;

public interface IntervalCollector
{
	void collect(long time, boolean handeledEvents, int scheduledJobs);

	void finish();

	void init();

	public final IntervalCollector NO_OP = new IntervalCollector()
	{
		@Override
		public void collect(long time, boolean handeledEvents, int scheduledJobs)
		{
		}

		@Override
		public void finish()
		{
		}

		@Override
		public void init()
		{
		}
	};
}