package sim.scheduling.job_comparators;

import sim.model.Job;

public final class HighestMemoryFirst implements JobComparator
{
	@Override
	public int compare(Job o1, Job o2)
	{
		return -Double.compare(o1.memory(), o2.memory());
	}
}