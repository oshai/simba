package sim.scheduling.max_cost;

import java.util.List;

import sim.scheduling.reserving.ScheduleCostResult;
import sim.scheduling.waiting_queue.WaitingQueue;

public interface IMaxCostCollector
{

	IMaxCostCollector NO_OP = new IMaxCostCollector()
	{
		@Override
		public void init()
		{
		}

		@Override
		public void collect(long time, Iterable<ScheduleCostResult> results, List<ScheduleCostResult> winner, WaitingQueue waitingQueue)
		{
		}
	};

	void collect(long time, Iterable<ScheduleCostResult> results, List<ScheduleCostResult> winner, WaitingQueue waitingQueue);

	void init();

}
