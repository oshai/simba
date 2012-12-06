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
		HostScheduler tested = new HostScheduler(null, mock(JobDispatcher.class));
		assertEquals(0, tested.schedule(1));
	}

	@Test
	public void testHasPotentialResourceFor()
	{
		Host host = mock(Host.class);
		Job job = Job.create(1).build();
		HostScheduler tested = new HostScheduler(host, null);
		tested.hasPotentialResourceFor(job);
		verify(host).hasPotentialResourceFor(job);
	}

	@Test
	public void testSchedule1JobOnHost()
	{
		Host host = Host.create().build();
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		HostScheduler tested = new HostScheduler(host, dispatcher);
		Job job = Job.create(1).build();
		tested.addJob(job);
		assertEquals(1, tested.schedule(1));
		verify(dispatcher).dispatch(job, host, 1);
	}

	@Test
	public void testSchedule1JobOnHostCannotEnter()
	{
		Host host = Host.create().cores(1.0).build();
		HostScheduler tested = new HostScheduler(host, mock(JobDispatcher.class));
		Job job = Job.create(1).cores(2.0).build();
		tested.addJob(job);
		assertEquals(0, tested.schedule(1));
		Job job1 = Job.create(1).memory(1.0).build();
		tested.addJob(job1);
		assertEquals(0, tested.schedule(1));
	}

	@Test
	public void testSchedule1JobTwice()
	{
		Host host = Host.create().cores(2.0).build();
		Job jobOnHost = Job.create(1).cores(1.0).build();
		host.dispatchJob(jobOnHost);
		HostScheduler tested = new HostScheduler(host, new JobDispatcher(mock(EventQueue.class)));
		Job job = Job.create(1).cores(2.0).build();
		tested.addJob(job);
		assertEquals(0, tested.schedule(1));
		host.finishJob(jobOnHost);
		assertEquals(1, tested.schedule(1));
		host.finishJob(job);
		assertEquals(0, tested.schedule(1));
	}

	@Test
	public void testAddJob()
	 throws Exception {
	
	}

}
