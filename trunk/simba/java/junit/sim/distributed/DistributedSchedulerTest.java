package sim.distributed;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sim.model.Job;
import sim.scheduling.LinkedListWaitingQueue;
import sim.scheduling.SetWaitingQueue;

import com.google.common.collect.Lists;

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
		DistributedScheduler tested = createScheduler(waitingQueue, hostSchedulers, hostSelector);
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
		DistributedScheduler tested = createScheduler(waitingQueue, hostSchedulers, hostSelector);
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
		DistributedScheduler tested = createScheduler(waitingQueue, hostSchedulers, hostSelector);
		when(hostSelector.select(job)).thenReturn(null);
		int scheduledSessions = 0;
		int time = 0;
		assertEquals(scheduledSessions, tested.schedule(time));
		assertEquals(1, waitingQueue.size());
		verify(hostSelector).select(job);
	}

	private DistributedScheduler createScheduler(LinkedListWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector)
	{
		return createScheduler(waitingQueue, hostSchedulers, hostSelector, new SetWaitingQueue());
	}

	private DistributedScheduler createScheduler(LinkedListWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector, SetWaitingQueue distributedWaitingJobs)
	{
		return new DistributedScheduler(waitingQueue, hostSchedulers, hostSelector, distributedWaitingJobs);
	}

	@Test
	public void testVirusJobShouldWaitOn1Hosts() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(15), mock(HostSelector.class), distributedWaitingJobs);
		tested.schedule(time + DistributedScheduler.VIRUS_TIME);
		assertTrue(waitingQueue.contains(job));
	}

	private ArrayList<HostScheduler> createHostSchedulers(int size)
	{
		ArrayList<HostScheduler> $ = Lists.<HostScheduler> newArrayList();
		for (int i = 0; i < size; i++)
		{
			$.add(mock(HostScheduler.class));
		}
		return $;
	}

	@Test
	public void testVirusJobShouldWaitOn8Hosts() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		int pow = (int) Math.pow(DistributedScheduler.VIRUS_POWER, 3);
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(pow + 5), mock(HostSelector.class), distributedWaitingJobs);
		tested.schedule(time + 4 * DistributedScheduler.VIRUS_TIME);
		assertEquals(pow, waitingQueue.size());
	}

	@Test
	public void testVirusJobShouldNotWaitOnMoreThanConfiguredHostsWaitOn8Hosts() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(0), mock(HostSelector.class), distributedWaitingJobs);
		tested.schedule(time + 4 * DistributedScheduler.VIRUS_TIME);
		assertEquals(0, waitingQueue.size());
	}

	private SetWaitingQueue createDitributedWaitingQueue(Job job)
	{
		SetWaitingQueue distributedWaitingJobs = new SetWaitingQueue();
		distributedWaitingJobs.add(job);
		return distributedWaitingJobs;
	}

}
