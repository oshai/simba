package sim.scheduling;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.*;

import sim.*;
import sim.event_handling.*;
import sim.model.*;
import sim.scheduling.graders.*;
import sim.scheduling.reserving.*;

public class ReservingSchedulerTest
{

	@Test
	public void testJobMatchHost_BugNoDispatch()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = mock(Job.class);
		waitingQueue.add(job);
		Cluster cluster = new Cluster();
		Host host = createHost(job);
		cluster.add(host);
		Grader grader = mock(Grader.class);
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		ForTestingSimbaConfiguration simbaConfiguration = new ForTestingSimbaConfiguration()
		{
			@Override
			public int jobsCheckedBySchduler()
			{
				return 1;
			}
		};
		Scheduler scheduler = createScheduler(waitingQueue, cluster, grader, dispatcher, simbaConfiguration);
		long time = 1;
		scheduler.schedule(time);
		assertTrue(waitingQueue.isEmpty());
		verify(dispatcher).dispatch(job, host, time);
	}

	@Test
	public void testJobMatchHost()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = mock(Job.class);
		waitingQueue.add(job);
		Cluster cluster = new Cluster();
		Host host = createHost(job);
		cluster.add(host);
		Grader grader = mock(Grader.class);
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		ReservingScheduler scheduler = createScheduler(waitingQueue, cluster, grader, dispatcher);
		long time = 1;
		scheduler.schedule(time);
		assertTrue(waitingQueue.isEmpty());
		verify(dispatcher).dispatch(job, host, time);
		assertNotNull(scheduler.grader());
	}

	@Test
	public void testJobMatchHostButShouldNotBeCheckedByScheduler()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = mock(Job.class);
		waitingQueue.add(job);
		Cluster cluster = new Cluster();
		Host host = createHost(job);
		cluster.add(host);
		Grader grader = mock(Grader.class);
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		ForTestingSimbaConfiguration simbaConfiguration = new ForTestingSimbaConfiguration()
		{
			@Override
			public int jobsCheckedBySchduler()
			{
				return 0;
			}
		};
		Scheduler scheduler = createScheduler(waitingQueue, cluster, grader, dispatcher, simbaConfiguration);
		long time = 1;
		scheduler.schedule(time);
		assertFalse(waitingQueue.isEmpty());
		verify(dispatcher, never()).dispatch(job, host, time);
	}

	private ReservingScheduler createScheduler(AbstractWaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher)
	{
		ForTestingSimbaConfiguration simbaConfiguration = new ForTestingSimbaConfiguration();
		return createScheduler(waitingQueue, cluster, grader, dispatcher, simbaConfiguration);
	}

	private ReservingScheduler createScheduler(AbstractWaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher, ForTestingSimbaConfiguration simbaConfiguration)
	{
		return new ReservingScheduler(waitingQueue, cluster, grader, dispatcher, simbaConfiguration);
	}

	private Host createHost(Job job)
	{
		Host host = mock(Host.class);
		when(host.id()).thenReturn("id");
		when(host.availableMemory()).thenReturn(1.0);
		when(host.availableCores()).thenReturn(1.0);
		when(host.hasAvailableResourcesFor(job)).thenReturn(true);
		return host;
	}

	@Test
	public void testJobMatchNothing()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(1).cores(2).priority(1).build();
		waitingQueue.add(job);
		Cluster cluster = new Cluster();
		Host host = Host.builder().id("1").cores(1).build();
		cluster.add(host);
		Grader grader = mock(Grader.class);
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		Scheduler scheduler = createScheduler(waitingQueue, cluster, grader, dispatcher);
		scheduler.schedule(0);
		assertEquals(1, waitingQueue.size());
		assertEquals(job, waitingQueue.peek());
	}

	@Test
	public void testJob2IsReserving()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job0 = Job.builder(1).cores(1).memory(1).priority(0).build();
		Job job1 = Job.builder(1).cores(1).memory(2).priority(1).build();
		Job job2 = Job.builder(1).cores(1).memory(1).priority(2).build();
		waitingQueue.add(job0);
		waitingQueue.add(job1);
		waitingQueue.add(job2);
		Cluster cluster = new Cluster();
		Host host = Host.builder().id("1").cores(3).memory(2).build();
		cluster.add(host);
		Grader grader = mock(Grader.class);
		JobDispatcher dispatcher = new JobDispatcher(mock(EventQueue.class));
		Scheduler scheduler = createScheduler(waitingQueue, cluster, grader, dispatcher, 2);
		scheduler.schedule(0);
		assertEquals(2, waitingQueue.size());
		Iterator<Job> iterator = waitingQueue.iterator();
		assertEquals(job1, iterator.next());
		assertEquals(job2, iterator.next());
	}

	@Test
	public void testJob2NotReserving()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job0 = Job.builder(1).cores(1).memory(1).priority(0).build();
		Job job1 = Job.builder(1).cores(1).memory(3).priority(1).build();
		Job job2 = Job.builder(1).cores(1).memory(1).priority(2).build();
		waitingQueue.add(job0);
		waitingQueue.add(job1);
		waitingQueue.add(job2);
		Cluster cluster = new Cluster();
		Host host = Host.builder().id("1").cores(3).memory(2).build();
		cluster.add(host);
		Grader grader = mock(Grader.class);
		JobDispatcher dispatcher = new JobDispatcher(mock(EventQueue.class));
		int reservationsLimit = 1;
		Scheduler scheduler = createScheduler(waitingQueue, cluster, grader, dispatcher, reservationsLimit);
		scheduler.schedule(0);
		assertEquals(1, waitingQueue.size());
		Iterator<Job> iterator = waitingQueue.iterator();
		assertEquals(job1, iterator.next());
	}

	@Test
	public void test2ReservingSameHost()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job0 = Job.builder(1).cores(1).memory(1).priority(0).build();
		Job job1 = Job.builder(1).cores(1).memory(2).priority(1).build();
		Job job2 = Job.builder(1).cores(1).memory(1).priority(2).build();
		Job job3 = Job.builder(1).cores(1).memory(1).priority(3).build();
		waitingQueue.add(job0);
		waitingQueue.add(job1);
		waitingQueue.add(job2);
		waitingQueue.add(job3);
		Cluster cluster = new Cluster();
		Host host = Host.builder().id("1").cores(3).memory(2).build();
		cluster.add(host);
		Grader grader = mock(Grader.class);
		JobDispatcher dispatcher = new JobDispatcher(mock(EventQueue.class));
		int reservationsLimit = 2;
		Scheduler scheduler = createScheduler(waitingQueue, cluster, grader, dispatcher, reservationsLimit);
		scheduler.schedule(0);
		assertEquals(3, waitingQueue.size());
		Iterator<Job> iterator = waitingQueue.iterator();
		assertEquals(job1, iterator.next());
		assertEquals(job2, iterator.next());
		assertEquals(job3, iterator.next());
	}

	private ReservingScheduler createScheduler(AbstractWaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher, final int reservationsLimit)
	{
		return new ReservingScheduler(waitingQueue, cluster, grader, dispatcher, new ForTestingSimbaConfiguration()
		{
			@Override
			public int reservationsLimit()
			{
				return reservationsLimit;
			}
		});
	}

	@Test
	public void testRunOnHighestGradeHost()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = mock(Job.class);
		waitingQueue.add(job);
		Cluster cluster = new Cluster();
		Host host1 = createHost(job);
		Host host2 = createHost(job);
		cluster.add(host1);
		cluster.add(host2);
		Grader grader = mock(Grader.class);
		when(grader.getGrade(host1, job)).thenReturn(0.0);
		when(grader.getGrade(host2, job)).thenReturn(1.0);
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		Scheduler scheduler = createScheduler(waitingQueue, cluster, grader, dispatcher);
		scheduler.schedule(0);
		assertEquals(0, waitingQueue.size());
		verify(dispatcher).dispatch(job, host2, 0);
	}

	@Test
	public void testReservationIsOnHighestMemoryHost()
	{
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job0 = Job.builder(1).priority(0).memory(2).build();
		Job job1 = Job.builder(1).priority(1).memory(4).build();
		Job job2 = Job.builder(1).priority(2).memory(3).build();
		Job job3 = Job.builder(1).priority(3).memory(1).build();
		waitingQueue.add(job0);
		waitingQueue.add(job1);
		waitingQueue.add(job2);
		waitingQueue.add(job3);
		Cluster cluster = new Cluster();
		Host host1 = Host.builder().id("1").cores(1).memory(3).build();
		Host host2 = Host.builder().id("2").cores(1).memory(6).build();
		cluster.add(host1);
		cluster.add(host2);
		Grader grader = mock(Grader.class);
		JobDispatcher dispatcher = new JobDispatcher(mock(EventQueue.class));
		Scheduler scheduler = createScheduler(waitingQueue, cluster, grader, dispatcher);
		scheduler.schedule(0);
		assertEquals(1, waitingQueue.size());
		assertEquals(job2, waitingQueue.remove());
	}
}
