package sim.scheduling;

import java.util.LinkedList;
import java.util.Queue;

import sim.model.Job;

public class LinkedListWaitingQueue extends AbstractWaitingQueue
{
	private final Queue<Job> queue = new LinkedList<Job>();

	@Override
	public Queue<Job> getQueue()
	{
		return queue;
	}

}
