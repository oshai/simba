package sim.distributed;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sim.DistributedSimbaConfiguration;
import sim.ForTestingSimbaConfiguration;
import sim.model.Job;
import sim.scheduling.LinkedListWaitingQueue;
import sim.scheduling.SetWaitingQueue;

import com.google.common.collect.Lists;

public class ExpandingDistributedSchedulerTest
{

	public class ForTestingDistributedSimbaConfiguration extends ForTestingSimbaConfiguration implements DistributedSimbaConfiguration
	{
		@Override
		public long timeToSchedule()
		{
			return 10;
		}

		@Override
		public double virusPower()
		{
			return 10;
		}

		@Override
		public long virusTime()
		{
			return 10;
		}
	}

	@Test
	public void testOneJob() throws Exception
	{
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).build();
		waitingQueue.add(job);
		List<HostScheduler> hostSchedulers = newArrayList();
		HostScheduler hostScheduler = mock(HostScheduler.class);
		hostSchedulers.add(hostScheduler);
		CyclicHostSelector hostSelector = mock(CyclicHostSelector.class);
		ExpandingDistributedScheduler tested = createScheduler(waitingQueue, hostSchedulers, hostSelector);
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
	public void testShouldBeRemovedFromWaitingIfAlreadyDistributedAndthereIsNoOtherHostThatCanAccept() throws Exception
	{
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		int time = 7;
		Job job = Job.builder(100).submitTime(time).build();
		waitingQueue.add(job);
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue();
		distributedWaitingJobs.add(job);
		ExpandingDistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(0), mock(CyclicHostSelector.class), distributedWaitingJobs, new ForTestingDistributedSimbaConfiguration());
		tested.distributeJobs(time);
		assertEquals(1, distributedWaitingJobs.size());
		assertEquals(0, waitingQueue.size());
	}

	@Test(expected = AssertionError.class)
	public void testOneJobNoHosts() throws Exception
	{
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).build();
		waitingQueue.add(job);
		List<HostScheduler> hostSchedulers = newArrayList();
		HostSelector hostSelector = mock(CyclicHostSelector.class);
		ExpandingDistributedScheduler tested = createScheduler(waitingQueue, hostSchedulers, hostSelector);
		when(hostSelector.select(job)).thenReturn(null);
		int scheduledSessions = 0;
		int time = 7;
		assertEquals(scheduledSessions, tested.schedule(time));
	}

	@Test
	public void testTime0() throws Exception
	{
		int time = 0;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).build();
		waitingQueue.add(job);
		HostScheduler h = mock(HostScheduler.class);
		List<HostScheduler> hostSchedulers = newArrayList(h);
		CyclicHostSelector hostSelector = mock(CyclicHostSelector.class);
		ExpandingDistributedScheduler tested = createScheduler(waitingQueue, hostSchedulers, hostSelector);
		when(hostSelector.select(job)).thenReturn(h);
		when(h.schedule(time)).thenReturn(1);
		assertEquals(1, tested.schedule(time));
		assertEquals(0, waitingQueue.size());
		verify(hostSelector).select(job);
	}

	private ExpandingDistributedScheduler createScheduler(LinkedListWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector)
	{
		return createScheduler(waitingQueue, hostSchedulers, hostSelector, new SetWaitingQueue(), new ForTestingDistributedSimbaConfiguration());
	}

	private ExpandingDistributedScheduler createScheduler(LinkedListWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector, SetWaitingQueue distributedWaitingJobs, ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration)
	{
		return new ExpandingDistributedScheduler(waitingQueue, hostSchedulers, hostSelector, distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
	}

	@Test
	public void testVirusJobShouldWaitOn1Hosts() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration = new ForTestingDistributedSimbaConfiguration();
		ExpandingDistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(15), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		tested.scheduleWaitingJobsAgain(time + forTestingDistributedSimbaConfiguration.virusTime());
		assertTrue(waitingQueue.contains(job));
	}

	@Test
	public void testVirusJobShouldWaitOn1Hosts1() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration = new ForTestingDistributedSimbaConfiguration();
		ExpandingDistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(15), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		tested.scheduleWaitingJobsAgain(10 + forTestingDistributedSimbaConfiguration.virusTime());
		assertTrue(waitingQueue.contains(job));
	}

	@Test
	public void testVirusJobShouldWaitOn1Hosts2() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration = new ForTestingDistributedSimbaConfiguration()
		{
			@Override
			public long virusTime()
			{
				return 100;
			}
		};
		ExpandingDistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(15), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		tested.scheduleWaitingJobsAgain(20);
		assertFalse(waitingQueue.contains(job));
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
		ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration = new ForTestingDistributedSimbaConfiguration();
		int pow = (int) Math.pow(forTestingDistributedSimbaConfiguration.virusPower(), 3);
		ExpandingDistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(pow + 5), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		tested.scheduleWaitingJobsAgain(time + 4 * forTestingDistributedSimbaConfiguration.virusTime());
		assertEquals(pow, waitingQueue.size());
	}

	@Test
	public void testVirusJobShouldNotWaitOnMoreThanConfiguredHostsWaitOn8Hosts() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration = new ForTestingDistributedSimbaConfiguration();
		ExpandingDistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(0), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		tested.schedule(time + 4 * forTestingDistributedSimbaConfiguration.virusTime());
		assertEquals(0, waitingQueue.size());
	}

	private SetWaitingQueue createDitributedWaitingQueue(Job... jobs)
	{
		SetWaitingQueue distributedWaitingJobs = new SetWaitingQueue();
		for (Job job : jobs)
		{
			distributedWaitingJobs.add(job);
		}
		return distributedWaitingJobs;
	}

}
