package sim.scheduling.waiting_queue;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Job;
import sim.scheduling.waiting_queue.SortedWaitingQueue;

public class SortedWaitingQueueTest
{

	@Test
	public void test()
	{
		SortedWaitingQueue tested = new SortedWaitingQueue();
		Job job1 = Job.builder(1).memory(2).build();
		tested.add(job1);
		Job job2 = Job.builder(1).memory(1).build();
		tested.add(job2);
		assertEquals(job2, tested.peek());
	}

	@Test
	public void testEquals()
	{
		SortedWaitingQueue tested = new SortedWaitingQueue();
		Job job1 = Job.builder(1).memory(1).build();
		tested.add(job1);
		Job job2 = Job.builder(1).memory(1).build();
		tested.add(job2);
		assertEquals(job1, tested.peek());
	}

}
