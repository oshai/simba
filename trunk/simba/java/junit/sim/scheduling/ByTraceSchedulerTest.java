package sim.scheduling;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;

public class ByTraceSchedulerTest
{

	@Test
	public void testJobDispatched()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(1).startTime(1).build();
		waitingQueue.add(job);
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		Scheduler tested = new ByTraceScheduler(waitingQueue, new Cluster(), dispatcher);
		long time = 1;
		tested.schedule(time);
		assertTrue(waitingQueue.isEmpty());
		verify(dispatcher).dispatch(eq(job), (Host) any(), eq(time));
	}

	@Test
	public void testHost()
	{
		Cluster cluster = new Cluster();
		cluster.add(Host.builder().cores(1.0).memory(2.0).build());
		cluster.add(Host.builder().cores(0.5).memory(0.5).build());
		new ByTraceScheduler(null, cluster, null);
		assertEquals(1, cluster.hosts().size());
		assertEquals(1.5, cluster.hosts().get(0).cores(), 0.1);
		assertEquals(2.5, cluster.hosts().get(0).memory(), 0.1);
	}

	@Test
	public void testJobNotDispatched()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(1).startTime(1).build();
		waitingQueue.add(job);
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		Scheduler tested = new ByTraceScheduler(waitingQueue, new Cluster(), dispatcher);
		long time = 0;
		tested.schedule(time);
		assertEquals(1, waitingQueue.size());
	}

}
