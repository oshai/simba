package sim.distributed;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Comparator;

import org.junit.Test;

import sim.event_handling.EventQueue;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.JobDispatcher;
import sim.scheduling.LinkedListWaitingQueue;
import sim.scheduling.SetWaitingQueue;
import sim.scheduling.job_comparators.ConstantComparator;
import sim.scheduling.job_comparators.HigestMemoryFirst;

import com.google.common.collect.Lists;

public class HostSchedulerTest
{

	@Test
	public void testEmpty()
	{
		Host host = mock(Host.class);
		HostScheduler tested = createHostScheduler(host, mock(JobDispatcher.class), new LinkedListWaitingQueue());
		assertEquals(0, tested.schedule(1));
		assertEquals(host, tested.host());
	}

	@Test
	public void testJobCannotRunBecauseOfResourcesIsRemovedFromWaitingQueue()
	{
		Host host = Host.builder().build();
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(1).cores(1.0).build();
		waitingQueue.add(job);
		HostScheduler tested = createHostScheduler(host, mock(JobDispatcher.class), waitingQueue);
		assertEquals(0, tested.schedule(1));
		assertEquals(0, waitingQueue.size());
	}

	@Test
	public void testIsAllowedToAddJobBecauseOfResources()
	{
		Host host = mock(Host.class);
		Job job = Job.builder(1).build();
		when(host.hasPotentialResourceFor(job)).thenReturn(true);
		HostScheduler tested = createHostScheduler(host, null, new LinkedListWaitingQueue());
		assertTrue(tested.isAllowedToAddJob(job));
	}

	@Test
	public void testIsAllowedToAddJobBecauseOfWaitingJobs()
	{
		Host host = mock(Host.class);
		Job job = Job.builder(1).build();
		when(host.hasPotentialResourceFor(job)).thenReturn(true);
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		waitingQueue.add(job);
		HostScheduler tested = createHostScheduler(host, null, waitingQueue);
		assertFalse(tested.isAllowedToAddJob(job));
	}

	@Test
	public void testSchedule1JobOnHost()
	{
		Host host = Host.builder().build();
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		SetWaitingQueue distributedWaitingJobs = new SetWaitingQueue();
		Job job = Job.builder(1).build();
		distributedWaitingJobs.add(job);
		HostScheduler tested = createHostScheduler(host, dispatcher, new LinkedListWaitingQueue(), distributedWaitingJobs, new HigestMemoryFirst());
		tested.addJob(job);
		assertEquals(1, tested.schedule(1));
		verify(dispatcher).dispatch(job, host, 1);
	}

	@Test
	public void testSchedule1JobOnHostCannotEnter()
	{
		Host host = Host.builder().cores(1.0).build();
		HostScheduler tested = createHostScheduler(host, mock(JobDispatcher.class), new LinkedListWaitingQueue());
		Job job = Job.builder(1).cores(2.0).build();
		tested.addJob(job);
		assertEquals(0, tested.schedule(1));
		Job job1 = Job.builder(1).memory(1.0).build();
		tested.addJob(job1);
		assertEquals(0, tested.schedule(1));
		assertEquals(0, tested.waitingJobsSize());
	}

	@Test
	public void testSchedule1JobOnHostExistOnDistributedQueue()
	{
		Host host = Host.builder().cores(1.0).build();
		Job job = Job.builder(1).cores(1.0).build();
		SetWaitingQueue distributedWaitingJobs = new SetWaitingQueue();
		HostScheduler tested = createHostScheduler(host, mock(JobDispatcher.class), new LinkedListWaitingQueue(), distributedWaitingJobs, new HigestMemoryFirst());
		tested.addJob(job);
		assertEquals(0, tested.schedule(1));
		assertEquals(0, tested.waitingJobsSize());
	}

	@Test
	public void testSchedule1JobTwice()
	{
		Host host = Host.builder().cores(2.0).build();
		Job jobOnHost = Job.builder(1).cores(1.0).build();
		host.dispatchJob(jobOnHost);
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		SetWaitingQueue distributedWaitingJobs = new SetWaitingQueue();
		Job job = Job.builder(1).cores(2.0).build();
		distributedWaitingJobs.add(job);
		HostScheduler tested = createHostScheduler(host, new JobDispatcher(mock(EventQueue.class)), waitingQueue, distributedWaitingJobs, new HigestMemoryFirst());
		waitingQueue.add(job);
		assertEquals(0, tested.schedule(1));
		assertEquals(1, waitingQueue.size());
		host.finishJob(jobOnHost);
		assertEquals(1, tested.schedule(1));
		host.finishJob(job);
		assertEquals(0, tested.schedule(1));
	}

	@Test
	public void testRemove()
	{
		AbstractWaitingQueue w = mock(AbstractWaitingQueue.class);
		when(w.iterator()).thenReturn(Lists.<Job> newArrayList().iterator());
		HostScheduler tested = createHostScheduler(null, new JobDispatcher(mock(EventQueue.class)), w);
		when(w.collectRemove()).thenReturn(12);
		assertEquals(12, tested.collectRemove());
	}

	@Test
	public void testAdd()
	{
		AbstractWaitingQueue w = mock(AbstractWaitingQueue.class);
		when(w.iterator()).thenReturn(Lists.<Job> newArrayList().iterator());
		HostScheduler tested = createHostScheduler(null, new JobDispatcher(mock(EventQueue.class)), w);
		when(w.collectAdd()).thenReturn(12);
		assertEquals(12, tested.collectAdd());
	}

	@Test
	public void testAddJob()
	{
		AbstractWaitingQueue w = new LinkedListWaitingQueue();
		HostScheduler tested = createHostScheduler(null, new JobDispatcher(mock(EventQueue.class)), w);
		assertEquals(w, tested.waitingJobs());
	}

	@Test
	public void testOrderJobsByNumberOfMemory() throws Exception
	{
		Host host = Host.builder().memory(2.0).build();
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		SetWaitingQueue distributedWaitingJobs = new SetWaitingQueue();
		Job job = createAndAddJobToQueues(waitingQueue, distributedWaitingJobs, 1.0);
		Job largerJob = createAndAddJobToQueues(waitingQueue, distributedWaitingJobs, 2.0);
		HostScheduler tested = createHostScheduler(host, new JobDispatcher(mock(EventQueue.class)), waitingQueue, distributedWaitingJobs, new HigestMemoryFirst());
		assertEquals(1, tested.schedule(1));
		assertFalse(waitingQueue.getQueue().contains(largerJob));
		assertTrue(waitingQueue.getQueue().contains(job));
	}

	@Test
	// actually for coverage of constant comparator
	public void testOrderJobsByOrderOfInsertionToHost() throws Exception
	{
		Host host = Host.builder().memory(2.0).build();
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		SetWaitingQueue distributedWaitingJobs = new SetWaitingQueue();
		Job job = createAndAddJobToQueues(waitingQueue, distributedWaitingJobs, 1.0);
		Job largerJob = createAndAddJobToQueues(waitingQueue, distributedWaitingJobs, 2.0);
		HostScheduler tested = createHostScheduler(host, new JobDispatcher(mock(EventQueue.class)), waitingQueue, distributedWaitingJobs, new ConstantComparator<Job>());
		assertEquals(1, tested.schedule(1));
		assertTrue(waitingQueue.getQueue().contains(largerJob));
		assertFalse(waitingQueue.getQueue().contains(job));
	}

	private Job createAndAddJobToQueues(LinkedListWaitingQueue waitingQueue, SetWaitingQueue distributedWaitingJobs, double memory)
	{
		Job job = Job.builder(1).memory(memory).build();
		distributedWaitingJobs.add(job);
		waitingQueue.add(job);
		return job;
	}

	private HostScheduler createHostScheduler(Host host, JobDispatcher dispatcher, AbstractWaitingQueue waitingQueue)
	{
		SetWaitingQueue distributedWaitingJobs = new SetWaitingQueue();
		return createHostScheduler(host, dispatcher, waitingQueue, distributedWaitingJobs, new HigestMemoryFirst());
	}

	private HostScheduler createHostScheduler(Host host, JobDispatcher dispatcher, AbstractWaitingQueue waitingQueue, SetWaitingQueue distributedWaitingJobs, Comparator<Job> jobGrader)
	{
		return new HostScheduler(host, dispatcher, waitingQueue, distributedWaitingJobs, jobGrader);
	}

}
