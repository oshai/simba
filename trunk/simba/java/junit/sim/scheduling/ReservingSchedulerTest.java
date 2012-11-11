package sim.scheduling;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Iterator;

import org.junit.Test;

import sim.event_handling.EventQueue;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.graders.Grader;

public class ReservingSchedulerTest
{

	@Test
	public void testJobMatchHost()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job = mock(Job.class);
		waitingQueue.add(job);
		Cluster cluster = new Cluster();
		Host host = mock(Host.class);
		when(host.hasAvailableResourcesFor(job)).thenReturn(true);
		cluster.add(host);
		Grader grader = mock(Grader.class);
		Dispatcher dispatcher = mock(Dispatcher.class);
		Scheduler scheduler = new ReservingScheduler(waitingQueue, cluster, grader, dispatcher);
		long time = 1;
		scheduler.schedule(time);
		assertTrue(waitingQueue.isEmpty());
		verify(dispatcher).dipatch(job, host, time);
	}

	@Test
	public void testJobMatchNothing()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job = Job.create(1).cores(2).priority(1).build();
		waitingQueue.add(job);
		Cluster cluster = new Cluster();
		Host host = Host.create().id("1").cores(1).build();
		cluster.add(host);
		Grader grader = mock(Grader.class);
		Dispatcher dispatcher = mock(Dispatcher.class);
		Scheduler scheduler = new ReservingScheduler(waitingQueue, cluster, grader, dispatcher);
		scheduler.schedule(0);
		assertEquals(1, waitingQueue.size());
		assertEquals(job, waitingQueue.peek());
	}

	@Test
	public void testJob2IsReserving()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job0 = Job.create(1).cores(1).memory(1).priority(0).build();
		Job job1 = Job.create(1).cores(1).memory(2).priority(1).build();
		Job job2 = Job.create(1).cores(1).memory(1).priority(2).build();
		waitingQueue.add(job0);
		waitingQueue.add(job1);
		waitingQueue.add(job2);
		Cluster cluster = new Cluster();
		Host host = Host.create().id("1").cores(3).memory(2).build();
		cluster.add(host);
		Grader grader = mock(Grader.class);
		Dispatcher dispatcher = new Dispatcher(mock(EventQueue.class));
		Scheduler scheduler = new ReservingScheduler(waitingQueue, cluster, grader, dispatcher);
		scheduler.schedule(0);
		assertEquals(2, waitingQueue.size());
		Iterator<Job> iterator = waitingQueue.iterator();
		assertEquals(job1, iterator.next());
		assertEquals(job2, iterator.next());
	}

	@Test
	public void test2ReservingSameHost()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job0 = Job.create(1).cores(1).memory(1).priority(0).build();
		Job job1 = Job.create(1).cores(1).memory(2).priority(1).build();
		Job job2 = Job.create(1).cores(1).memory(1).priority(2).build();
		Job job3 = Job.create(1).cores(1).memory(1).priority(3).build();
		waitingQueue.add(job0);
		waitingQueue.add(job1);
		waitingQueue.add(job2);
		waitingQueue.add(job3);
		Cluster cluster = new Cluster();
		Host host = Host.create().id("1").cores(3).memory(2).build();
		cluster.add(host);
		Grader grader = mock(Grader.class);
		Dispatcher dispatcher = new Dispatcher(mock(EventQueue.class));
		Scheduler scheduler = new ReservingScheduler(waitingQueue, cluster, grader, dispatcher);
		scheduler.schedule(0);
		assertEquals(3, waitingQueue.size());
		Iterator<Job> iterator = waitingQueue.iterator();
		assertEquals(job1, iterator.next());
		assertEquals(job2, iterator.next());
		assertEquals(job3, iterator.next());
	}

	@Test
	public void testRunOnHighestGradeHost()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job = mock(Job.class);
		waitingQueue.add(job);
		Cluster cluster = new Cluster();
		Host host1 = mock(Host.class);
		Host host2 = mock(Host.class);
		cluster.add(host1);
		cluster.add(host2);
		Grader grader = mock(Grader.class);
		when(grader.getGrade(host1, job)).thenReturn(0.0);
		when(grader.getGrade(host2, job)).thenReturn(1.0);
		Dispatcher dispatcher = mock(Dispatcher.class);
		Scheduler scheduler = new ReservingScheduler(waitingQueue, cluster, grader, dispatcher);
		scheduler.schedule(0);
		assertEquals(0, waitingQueue.size());
		verify(dispatcher).dipatch(job, host2, 0);
	}

	@Test
	public void testReservationIsOnHighestMemoryHost()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job0 = Job.create(1).priority(0).memory(2).build();
		Job job1 = Job.create(1).priority(1).memory(4).build();
		Job job2 = Job.create(1).priority(2).memory(3).build();
		Job job3 = Job.create(1).priority(3).memory(1).build();
		waitingQueue.add(job0);
		waitingQueue.add(job1);
		waitingQueue.add(job2);
		waitingQueue.add(job3);
		Cluster cluster = new Cluster();
		Host host1 = Host.create().id("1").memory(3).build();
		Host host2 = Host.create().id("2").memory(6).build();
		cluster.add(host1);
		cluster.add(host2);
		Grader grader = mock(Grader.class);
		Dispatcher dispatcher = new Dispatcher(mock(EventQueue.class));
		Scheduler scheduler = new ReservingScheduler(waitingQueue, cluster, grader, dispatcher);
		scheduler.schedule(0);
		assertEquals(1, waitingQueue.size());
		assertEquals(job2, waitingQueue.remove());
	}
}
