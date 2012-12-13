package sim.scheduling;

import java.util.*;

import sim.model.*;

import com.google.common.collect.*;

public class SetWaitingQueue implements WaitingQueueForStatistics
{
	Set<Job> jobs = Sets.<Job> newHashSet();
	private int added = 0;
	private int removed = 0;

	@Override
	public Iterator<Job> iterator()
	{
		return new ForwardingIterator<Job>()
		{
			private Iterator<Job> iterator = jobs.iterator();

			@Override
			protected Iterator<Job> delegate()
			{
				return iterator;
			}

			@Override
			public void remove()
			{
				super.remove();
				removed++;
			}
		};
	}

	@Override
	public int collectRemove()
	{
		int $ = removed;
		removed = 0;
		return $;
	}

	@Override
	public int collectAdd()
	{
		int $ = added;
		added = 0;
		return $;
	}

	@Override
	public int size()
	{
		return jobs.size();
	}

	public void add(Job j)
	{
		if (jobs.add(j))
		{
			added++;
		}
	}

	public boolean remove(Job j)
	{
		boolean remove = jobs.remove(j);
		if (remove)
		{
			removed++;
		}
		return remove;
	}

}
