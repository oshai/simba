package sim.distributed;

import java.util.Iterator;
import java.util.List;

import sim.model.Job;

import com.google.common.collect.Iterators;

public class CyclicHostSelector implements HostSelector
{

	private Iterator<HostScheduler> cyclicIterator;

	public CyclicHostSelector(List<HostScheduler> hostScheduler)
	{
		this.cyclicIterator = Iterators.cycle(hostScheduler);
	}

	@Override
	public HostScheduler select(Job job)
	{
		HostScheduler firstHost = null;
		for (; cyclicIterator.hasNext();)
		{
			HostScheduler h = cyclicIterator.next();
			if (h.isAllowedToAddJob(job))
			{
				return h;
			}
			if (isFullCycle(firstHost, h))
			{
				return null;
			}
			if (null == firstHost)
			{
				firstHost = h;
			}
		}
		return null;
	}

	private boolean isFullCycle(HostScheduler firstHost, HostScheduler h)
	{
		return h.equals(firstHost);
	}

}
