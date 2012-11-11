package sim.scheduling;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import sim.model.Job;

public class WaitingQueue
{
	private Queue<Job> queue = new LinkedList<Job>();

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
	}

	public Job remove()
	{
		return queue.remove();
	}

	public Iterator<Job> iterator()
	{
		return queue.iterator();
	}
}
