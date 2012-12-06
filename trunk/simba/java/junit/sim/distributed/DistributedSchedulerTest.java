package sim.distributed;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;

import sim.model.Job;
import sim.scheduling.DistributedScheduler;
import sim.scheduling.HostScheduler;
import sim.scheduling.HostSelector;
import sim.scheduling.LinkedListWaitingQueue;

public class DistributedSchedulerTest
{

	@Test
	public void testOneJob() throws Exception
	{
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.create(100).build();
		waitingQueue.add(job);
		List<HostScheduler> hostSchedulers = newArrayList();
		HostScheduler hostScheduler = mock(HostScheduler.class);
		hostSchedulers.add(hostScheduler);
		HostSelector hostSelector = mock(HostSelector.class);
		DistributedScheduler tested = new DistributedScheduler(waitingQueue, hostSchedulers, hostSelector);
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
		Job job = Job.create(100).build();
		waitingQueue.add(job);
		List<HostScheduler> hostSchedulers = newArrayList();
		HostSelector hostSelector = mock(HostSelector.class);
		DistributedScheduler tested = new DistributedScheduler(waitingQueue, hostSchedulers, hostSelector);
		when(hostSelector.select(job)).thenReturn(null);
		int scheduledSessions = 0;
		int time = 7;
		assertEquals(scheduledSessions, tested.schedule(time));
		assertEquals(1, waitingQueue.size());
		verify(hostSelector).select(job);
	}

}
