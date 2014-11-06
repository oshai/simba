package sim.collectors;

import sim.model.Host;
import sim.model.Job;

public interface IJobCollector
{

	void collect(Host host, Job job);

	void finish();

	void init();

	public final IJobCollector NO_OP = new IJobCollector()
	{
		@Override
		public void collect(Host host, Job job)
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