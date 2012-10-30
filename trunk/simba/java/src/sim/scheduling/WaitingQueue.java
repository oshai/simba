package sim.scheduling;

import sim.model.Job;

import com.google.common.collect.MinMaxPriorityQueue;

public class WaitingQueue
{
	private MinMaxPriorityQueue<Job> queue = MinMaxPriorityQueue.orderedBy(new JobPriorityComparator()).create();

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
		return queue.peekFirst();
	}

	public void add(Job job)
	{
		queue.add(job);
	}

	public Job remove()
	{
		return queue.removeFirst();
	}
}
