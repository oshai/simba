package sim.scheduling.reserving;

public interface IMaxCostCollector
{

	void collect(long time, Iterable<ScheduleCostResult> results);

}
