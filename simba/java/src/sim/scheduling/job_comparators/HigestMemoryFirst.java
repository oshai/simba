package sim.scheduling.job_comparators;

import java.util.Comparator;

import sim.model.Job;

public final class HigestMemoryFirst implements Comparator<Job>
{
	@Override
	public int compare(Job o1, Job o2)
	{
		return -Double.compare(o1.memory(), o2.memory());
	}
}