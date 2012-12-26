package sim.scheduling.waiting_queue;

import java.util.Comparator;

import sim.model.Job;

final class JobLessMemoryComparator implements Comparator<Job>
{
	@Override
	public int compare(Job o1, Job o2)
	{
		if (o1.memory() == o2.memory())
		{
			return 0;
		}
		return o1.memory() - o2.memory() > 0 ? 1 : -1;
	}
}