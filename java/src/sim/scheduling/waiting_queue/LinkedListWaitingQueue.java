package sim.scheduling.waiting_queue;

import java.util.LinkedList;
import java.util.Queue;

import sim.model.Job;

public class LinkedListWaitingQueue extends AbstractWaitingQueue
{
	private final Queue<Job> queue = new LinkedList<Job>();

	@Override
	protected Queue<Job> getQueue()
	{
		return queue;
	}
}
