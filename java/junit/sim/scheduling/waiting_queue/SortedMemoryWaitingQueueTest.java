package sim.scheduling.waiting_queue;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Job;
import sim.scheduling.waiting_queue.SortedMemoryWaitingQueue;

public class SortedMemoryWaitingQueueTest
{

	@Test
	public void test()
	{
		SortedMemoryWaitingQueue tested = new SortedMemoryWaitingQueue();
		Job job1 = Job.builder(1).memory(2).build();
		tested.add(job1);
		Job job2 = Job.builder(1).memory(1).build();
		tested.add(job2);
		assertEquals(job2, tested.peek());
	}

	@Test
	public void testEquals()
	{
		SortedMemoryWaitingQueue tested = new SortedMemoryWaitingQueue();
		Job job1 = Job.builder(1).memory(1).build();
		tested.add(job1);
		Job job2 = Job.builder(1).memory(1).build();
		tested.add(job2);
		assertEquals(job1, tested.peek());
	}

}
