package sim.scheduling.waiting_queue;

import java.util.Comparator;
import java.util.Queue;

import sim.model.Job;

import com.google.common.collect.MinMaxPriorityQueue;

public class SortedMemoryWaitingQueue extends AbstractWaitingQueue
{
	private Comparator<Job> c = new JobLessMemoryComparator();

	private final Queue<Job> queue = MinMaxPriorityQueue.orderedBy(c).create();

	@Override
	protected Queue<Job> getQueue()
	{
		return queue;
	}

}
