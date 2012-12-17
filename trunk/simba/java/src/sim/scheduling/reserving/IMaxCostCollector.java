package sim.scheduling.reserving;

import java.util.List;

public interface IMaxCostCollector
{

	void collect(long time, Iterable<ScheduleCostResult> results, List<ScheduleCostResult> winner);

}
