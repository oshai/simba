package sim.scheduling.job_comparators;

import java.util.Comparator;

import sim.model.Job;

public class OldestJobFirst implements Comparator<Job>
{
	@Override
	public int compare(Job o1, Job o2)
	{
		return Long.signum(o1.submitTime() - o2.submitTime());
	}

}
