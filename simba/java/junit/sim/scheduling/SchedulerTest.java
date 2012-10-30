package sim.scheduling;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.first_fit.FirstFitScheduler;
import sim.scheduling.matchers.Matcher;

public class SchedulerTest
{
	
	@Test
	public void testJobMatchHost()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job = mock(Job.class);
		waitingQueue.add(job);
		Cluster cluster = new Cluster();
		Host host = Host.Builder.create().cores(0).memory(0).build();
		cluster.add(host);
		Matcher matcher = mock(Matcher.class);
		when(matcher.match(job, cluster.hosts())).thenReturn(host);
		Dispatcher dispatcher = mock(Dispatcher.class);
		Scheduler scheduler = new FirstFitScheduler(waitingQueue, cluster, matcher, dispatcher);
		long time = 1;
		scheduler.schedule(time);
		assertTrue(waitingQueue.isEmpty());
		verify(dispatcher).dipatch(job, host, time);
	}
	
	@Test
	public void testJobMatchNull()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job = mock(Job.class);
		waitingQueue.add(job);
		Cluster cluster = new Cluster();
		Host host = Host.Builder.create().cores(0).memory(0).build();
		cluster.add(host);
		Matcher matcher = mock(Matcher.class);
		Dispatcher dispatcher = mock(Dispatcher.class);
		Scheduler scheduler = new FirstFitScheduler(waitingQueue, cluster, matcher, dispatcher);
		scheduler.schedule(0);
		assertEquals(1, waitingQueue.size());
		assertEquals(job, waitingQueue.peek());
	}
	
}
