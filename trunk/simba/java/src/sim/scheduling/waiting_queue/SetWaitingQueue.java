package sim.scheduling.waiting_queue;

import java.util.Iterator;
import java.util.Set;

import sim.model.Job;

import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.Sets;

public class SetWaitingQueue implements WaitingQueueForStatistics
{
	private final Set<Job> jobs = Sets.newHashSet();
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

	public boolean contains(Job job)
	{
		return jobs.contains(job);
	}

}
