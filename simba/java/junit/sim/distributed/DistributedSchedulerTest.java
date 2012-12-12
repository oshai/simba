package sim.distributed;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import sim.model.Job;
import sim.scheduling.HostScheduler;
import sim.scheduling.HostSelector;
import sim.scheduling.LinkedListWaitingQueue;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DistributedSchedulerTest
{

	@Test
	public void testOneJob() throws Exception
	{
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).build();
		waitingQueue.add(job);
		List<HostScheduler> hostSchedulers = newArrayList();
		HostScheduler hostScheduler = mock(HostScheduler.class);
		hostSchedulers.add(hostScheduler);
		HostSelector hostSelector = mock(HostSelector.class);
		DistributedScheduler tested = new DistributedScheduler(waitingQueue, hostSchedulers, hostSelector, Sets.<Job> newHashSet());
		when(hostSelector.select(job)).thenReturn(hostScheduler);
		int scheduledSessions = 1;
		int time = 7;
		when(hostScheduler.schedule(time)).thenReturn(scheduledSessions);
		assertEquals(scheduledSessions, tested.schedule(time));
		assertTrue(waitingQueue.isEmpty());
		verify(hostScheduler).addJob(job);
		verify(hostScheduler).schedule(time);
		verify(hostSelector).select(job);
	}

	@Test
	public void testOneJobNoHosts() throws Exception
	{
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).build();
		waitingQueue.add(job);
		List<HostScheduler> hostSchedulers = newArrayList();
		HostSelector hostSelector = mock(HostSelector.class);
		DistributedScheduler tested = new DistributedScheduler(waitingQueue, hostSchedulers, hostSelector, Sets.<Job> newHashSet());
		when(hostSelector.select(job)).thenReturn(null);
		int scheduledSessions = 0;
		int time = 7;
		assertEquals(scheduledSessions, tested.schedule(time));
		assertEquals(1, waitingQueue.size());
		verify(hostSelector).select(job);
	}

	@Test
	public void testTime0() throws Exception
	{
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).build();
		waitingQueue.add(job);
		HostScheduler h = mock(HostScheduler.class);
		List<HostScheduler> hostSchedulers = newArrayList(h);
		HostSelector hostSelector = mock(HostSelector.class);
		DistributedScheduler tested = new DistributedScheduler(waitingQueue, hostSchedulers, hostSelector, Sets.<Job> newHashSet());
		when(hostSelector.select(job)).thenReturn(null);
		int scheduledSessions = 0;
		int time = 0;
		assertEquals(scheduledSessions, tested.schedule(time));
		assertEquals(1, waitingQueue.size());
		verify(hostSelector).select(job);
	}

	@Test
	public void testVirusJobShouldWaitOn1Hosts() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		HashSet<Job> distributedWaitingJobs = Sets.<Job> newHashSet();
		distributedWaitingJobs.add(job);
		DistributedScheduler tested = new DistributedScheduler(waitingQueue, Lists.<HostScheduler> newArrayList(), mock(HostSelector.class),
				distributedWaitingJobs);
		tested.schedule(time + DistributedScheduler.VIRUS_TIME);
		assertTrue(waitingQueue.contains(job));
	}

	@Test
	public void testVirusJobShouldWaitOn2Hosts() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		HashSet<Job> distributedWaitingJobs = Sets.<Job> newHashSet();
		distributedWaitingJobs.add(job);
		DistributedScheduler tested = new DistributedScheduler(waitingQueue, Lists.<HostScheduler> newArrayList(), mock(HostSelector.class),
				distributedWaitingJobs);
		tested.schedule(time + 2 * DistributedScheduler.VIRUS_TIME);
		assertEquals(job, waitingQueue.remove());
		assertEquals(job, waitingQueue.remove());
		assertTrue(waitingQueue.isEmpty());
	}

	@Test
	public void testVirusJobShouldWaitOn8Hosts() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		HashSet<Job> distributedWaitingJobs = Sets.<Job> newHashSet();
		distributedWaitingJobs.add(job);
		DistributedScheduler tested = new DistributedScheduler(waitingQueue, Lists.<HostScheduler> newArrayList(), mock(HostSelector.class),
				distributedWaitingJobs);
		tested.schedule(time + 4 * DistributedScheduler.VIRUS_TIME);
		assertEquals(8, waitingQueue.size());
	}

}
