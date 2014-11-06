package sim.scheduling.waiting_queue;

import sim.model.Job;

public interface WaitingQueue extends Iterable<Job>
{
	public boolean isEmpty();

	public int size();

	public Job peek();

	public void add(Job job);

	public Job remove();

}