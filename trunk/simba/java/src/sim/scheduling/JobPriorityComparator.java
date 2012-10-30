package sim.scheduling;

import java.util.Comparator;

import sim.model.Job;

public class JobPriorityComparator implements Comparator<Job>
{
	
	@Override
	public int compare(Job o1, Job o2)
	{
		if (o1.priority() == o2.priority())
		{
			return 0;
		}
		return o1.priority()-o2.priority() > 0 ? 1 : -1;
	}
	
}
