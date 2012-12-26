package sim.scheduling.job_comparators;

import java.util.Map;

import sim.collectors.CostStatistics;
import sim.collectors.Qslot;
import sim.model.Job;

public class FairShareComparator implements JobComparator
{
	private final CostStatistics statistics;

	public FairShareComparator(CostStatistics stats)
	{
		super();
		this.statistics = stats;
	}

	@Override
	public int compare(Job j1, Job j2)
	{
		Map<String, Qslot> qs = statistics.apply();
		return Double.compare(getJobShouldGet(qs, j1), getJobShouldGet(qs, j2));
	}

	private double getJobShouldGet(Map<String, Qslot> qs, Job j)
	{
		return qs.get(j.qslot()).relativeShouldGetDelta();
	}

}
