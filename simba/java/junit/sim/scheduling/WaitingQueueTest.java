package sim.scheduling;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Job;

public class WaitingQueueTest
{

	@Test
	public void test()
	{
		Job job1 = Job.create(1).priority(1).build();
		Job job2 = Job.create(1).priority(0).build();
		WaitingQueue waitingQueue = new WaitingQueue();
		waitingQueue.add(job1);
		waitingQueue.add(job2);
		assertEquals(job2, waitingQueue.peek());
		assertEquals(job2, waitingQueue.remove());
		assertEquals(job1, waitingQueue.remove());
	}

}
