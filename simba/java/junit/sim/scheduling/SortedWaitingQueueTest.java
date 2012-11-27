package sim.scheduling;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Job;

public class SortedWaitingQueueTest
{

	@Test
	public void test()
	{
		SortedWaitingQueue tested = new SortedWaitingQueue();
		Job job1 = Job.create(1).memory(2).build();
		tested.add(job1);
		Job job2 = Job.create(1).memory(1).build();
		tested.add(job2);
		assertEquals(job2, tested.peek());
	}

}
