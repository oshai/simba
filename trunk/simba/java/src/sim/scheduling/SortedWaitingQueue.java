package sim.scheduling;

import java.util.Comparator;
import java.util.Queue;

import sim.model.Job;

import com.google.common.collect.MinMaxPriorityQueue;

public class SortedWaitingQueue extends AbstractWaitingQueue
{
	private Comparator<Job> c = new Comparator<Job>()
	{
		@Override
		public int compare(Job o1, Job o2)
		{
			if (o1.memory() == o2.memory())
			{
				return 0;
			}
			return o1.memory() - o2.memory() > 0 ? 1 : -1;
		}
	};
	private final Queue<Job> queue = MinMaxPriorityQueue.orderedBy(c).create();

	@Override
	public Queue<Job> getQueue()
	{
		return queue;
	}

}
