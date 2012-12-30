package sim.scheduling.reserving;

import java.util.List;

public interface IMaxCostCollector
{

	IMaxCostCollector NO_OP = new IMaxCostCollector()
	{
		@Override
		public void init()
		{
		}

		@Override
		public void collect(long time, Iterable<ScheduleCostResult> results, List<ScheduleCostResult> winner)
		{
		}
	};

	void collect(long time, Iterable<ScheduleCostResult> results, List<ScheduleCostResult> winner);

	void init();

}
