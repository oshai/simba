package sim.scheduling;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import sim.model.Job;

import com.google.common.collect.ForwardingIterator;

public class WaitingQueue
{
	private Queue<Job> queue = new LinkedList<Job>();
	private int added;
	private int removed;

	public boolean isEmpty()
	{
		return queue.isEmpty();
	}

	public int size()
	{
		return queue.size();
	}

	public Job peek()
	{
		return queue.peek();
	}

	public void add(Job job)
	{
		queue.add(job);
		added++;
	}

	public Job remove()
	{
		removed++;
		return queue.remove();
	}

	public Iterator<Job> iterator()
	{
		return new ForwardingIterator<Job>()
		{
			private Iterator<Job> iterator = queue.iterator();

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

	public int collectAdd()
	{
		int $ = added;
		added = 0;
		return $;
	}

	public int collectRemove()
	{
		int $ = removed;
		removed = 0;
		return $;
	}

}
