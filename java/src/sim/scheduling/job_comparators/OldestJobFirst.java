package sim.scheduling.job_comparators;

import sim.model.Job;

public class OldestJobFirst implements JobComparator
{
	@Override
	public int compare(Job o1, Job o2)
	{
		return Long.signum(o1.submitTime() - o2.submitTime());
	}

}
