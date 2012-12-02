package sim.scheduling;

import java.util.Comparator;
import java.util.Queue;

import sim.model.Job;

import com.google.common.collect.MinMaxPriorityQueue;

public class SortedWaitingQueue extends AbstractWaitingQueue
{
	private Comparator<Job> c = new JobLessMemoryComparator();

	private final Queue<Job> queue = MinMaxPriorityQueue.orderedBy(c).create();

	@Override
	public Queue<Job> getQueue()
	{
		return queue;
	}

}
