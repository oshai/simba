package sim.collectors;

public interface IntervalCollector
{

	void collect(long time, boolean handeledEvents, int scheduledJobs);

	void finish();

	void init();

}