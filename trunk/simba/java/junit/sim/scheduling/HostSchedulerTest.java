package sim.scheduling;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.event_handling.EventQueue;
import sim.model.Host;
import sim.model.Job;

public class HostSchedulerTest
{

	@Test
	public void testEmpty()
	{
		HostScheduler tested = new HostScheduler(null, mock(JobDispatcher.class), new LinkedListWaitingQueue());
		assertEquals(0, tested.schedule(1));
	}

	@Test
	public void testHasPotentialResourceFor()
	{
		Host host = mock(Host.class);
		Job job = Job.builder(1).build();
		HostScheduler tested = new HostScheduler(host, null, new LinkedListWaitingQueue());
		tested.hasPotentialResourceFor(job);
		verify(host).hasPotentialResourceFor(job);
	}

	@Test
	public void testSchedule1JobOnHost()
	{
		Host host = Host.builder().build();
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		HostScheduler tested = new HostScheduler(host, dispatcher, new LinkedListWaitingQueue());
		Job job = Job.builder(1).build();
		tested.addJob(job);
		assertEquals(1, tested.schedule(1));
		verify(dispatcher).dispatch(job, host, 1);
	}

	@Test
	public void testSchedule1JobOnHostCannotEnter()
	{
		Host host = Host.builder().cores(1.0).build();
		HostScheduler tested = new HostScheduler(host, mock(JobDispatcher.class), new LinkedListWaitingQueue());
		Job job = Job.builder(1).cores(2.0).build();
		tested.addJob(job);
		assertEquals(0, tested.schedule(1));
		Job job1 = Job.builder(1).memory(1.0).build();
		tested.addJob(job1);
		assertEquals(0, tested.schedule(1));
		assertEquals(2, tested.waitingJobs());
	}

	@Test
	public void testSchedule1JobTwice()
	{
		Host host = Host.builder().cores(2.0).build();
		Job jobOnHost = Job.builder(1).cores(1.0).build();
		host.dispatchJob(jobOnHost);
		HostScheduler tested = new HostScheduler(host, new JobDispatcher(mock(EventQueue.class)), new LinkedListWaitingQueue());
		Job job = Job.builder(1).cores(2.0).build();
		tested.addJob(job);
		assertEquals(0, tested.schedule(1));
		host.finishJob(jobOnHost);
		assertEquals(1, tested.schedule(1));
		host.finishJob(job);
		assertEquals(0, tested.schedule(1));
	}

	@Test
	public void testRemove()
	{
		AbstractWaitingQueue w = mock(AbstractWaitingQueue.class);
		HostScheduler tested = new HostScheduler(null, new JobDispatcher(mock(EventQueue.class)), w);
		when(w.collectRemove()).thenReturn(12);
		assertEquals(12, tested.collectRemove());
	}

	@Test
	public void testAdd()
	{
		AbstractWaitingQueue w = mock(AbstractWaitingQueue.class);
		HostScheduler tested = new HostScheduler(null, new JobDispatcher(mock(EventQueue.class)), w);
		when(w.collectAdd()).thenReturn(12);
		assertEquals(12, tested.collectAdd());
	}

}
