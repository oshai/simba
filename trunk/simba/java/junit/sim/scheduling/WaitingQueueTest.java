package sim.scheduling;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import sim.model.Job;

public class WaitingQueueTest
{

	@Test
	public void test()
	{
		Job job1 = Job.create(1).priority(0).build();
		Job job2 = Job.create(1).priority(1).build();
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		waitingQueue.add(job1);
		waitingQueue.add(job2);
		assertEquals(job1, waitingQueue.peek());
		assertEquals(job1, waitingQueue.remove());
		assertEquals(job2, waitingQueue.remove());
	}

	@Test
	public void testIterator()
	{
		Job job0 = Job.create(1).priority(0).build();
		Job job1 = Job.create(1).priority(1).build();
		Job job2 = Job.create(1).priority(2).build();
		Job job3 = Job.create(1).priority(3).build();
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		waitingQueue.add(job0);
		waitingQueue.add(job1);
		waitingQueue.add(job2);
		waitingQueue.add(job3);
		Iterator<Job> iterator = waitingQueue.iterator();
		assertEquals(job0, iterator.next());
		assertEquals(job1, iterator.next());
		assertEquals(job2, iterator.next());
		assertEquals(job3, iterator.next());
	}

}
