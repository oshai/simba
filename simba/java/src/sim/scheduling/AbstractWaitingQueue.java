package sim.scheduling;

import java.util.Iterator;
import java.util.Queue;

import sim.model.Job;

import com.google.common.collect.ForwardingIterator;

public abstract class AbstractWaitingQueue
{

	private int added;
	private int removed;

	public AbstractWaitingQueue()
	{
		super();
	}

	public abstract Queue<Job> getQueue();

	public boolean isEmpty()
	{
		return getQueue().isEmpty();
	}

	public int size()
	{
		return getQueue().size();
	}

	public Job peek()
	{
		return getQueue().peek();
	}

	public void add(Job job)
	{
		getQueue().add(job);
		added++;
	}

	public Job remove()
	{
		removed++;
		return getQueue().remove();
	}

	public Iterator<Job> iterator()
	{
		return new ForwardingIterator<Job>()
		{
			private Iterator<Job> iterator = getQueue().iterator();

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