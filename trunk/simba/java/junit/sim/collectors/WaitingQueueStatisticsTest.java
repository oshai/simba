package sim.collectors;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.Clock;
import sim.model.Job;
import sim.scheduling.WaitingQueue;

public class WaitingQueueStatisticsTest
{

	@Test
	public void testEmpty()
	{
		WaitingQueue w = new WaitingQueue();
		Clock clock = new Clock();
		WaitingQueueStatistics tested = new WaitingQueueStatistics(w, 2, clock);
		tested.updateStatistics();
		assertEquals(0, tested.waitingJobs());
		assertEquals(0, tested.avgMemoryFront(), 0.1);
	}

	@Test
	public void testAvgMemory()
	{
		WaitingQueue w = new WaitingQueue();
		w.add(Job.create(1).memory(2).build());
		w.add(Job.create(1).memory(4).build());
		Clock clock = new Clock();
		WaitingQueueStatistics tested = new WaitingQueueStatistics(w, 2, clock);
		tested.updateStatistics();
		assertEquals(3, tested.avgMemoryFront(), 0.1);
	}

	@Test
	public void testAvgMemoryFront()
	{
		WaitingQueue w = new WaitingQueue();
		w.add(Job.create(1).memory(2).build());
		w.add(Job.create(1).memory(4).build());
		Clock clock = new Clock();
		WaitingQueueStatistics tested = new WaitingQueueStatistics(w, 1, clock);
		tested.updateStatistics();
		assertEquals(2, tested.avgMemoryFront(), 0.1);
	}

	@Test
	public void testAvgWaitTime()
	{
		WaitingQueue w = new WaitingQueue();
		w.add(Job.create(1).submitTime(0).build());
		w.add(Job.create(1).submitTime(1).build());
		Clock clock = new Clock(1);
		WaitingQueueStatistics tested = new WaitingQueueStatistics(w, 2, clock);
		tested.updateStatistics();
		assertEquals(0.5, tested.avgWaitTimeFront(), 0.1);
	}

	@Test
	public void testSubmittedDispatchedJobs()
	{
		WaitingQueue w = new WaitingQueue();
		Clock clock = new Clock();
		WaitingQueueStatistics tested = new WaitingQueueStatistics(w, 2, clock);
		w.add(Job.create(1).build());
		w.add(Job.create(1).build());
		w.remove();
		tested.updateStatistics();
		assertEquals(2, tested.submittedJobs());
		assertEquals(1, tested.dispatchedJobs());
		tested.updateStatistics();
		assertEquals(0, tested.submittedJobs());
		assertEquals(0, tested.dispatchedJobs());
	}

}
