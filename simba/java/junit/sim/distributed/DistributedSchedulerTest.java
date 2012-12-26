package sim.distributed;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import sim.DistributedSimbaConfiguration;
import sim.ForTestingSimbaConfiguration;
import sim.collectors.CostStatistics;
import sim.distributed.expanding_strategy.PowerExpandingStrategy;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.LinkedListWaitingQueue;
import sim.scheduling.SetWaitingQueue;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class DistributedSchedulerTest
{

	@Mock
	private CostStatistics costStatistics;

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

		@Override
		public int intialDispatchFactor()
		{
			return 1;
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
	public void testShouldBeRemovedFromWaitingIfAlreadyDistributedAndthereIsNoOtherHostThatCanAccept() throws Exception
	{
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		int time = 7;
		Job job = Job.builder(100).submitTime(time).build();
		waitingQueue.add(job);
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue();
		distributedWaitingJobs.add(job);
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(0), mock(CyclicHostSelector.class), distributedWaitingJobs, new ForTestingDistributedSimbaConfiguration());
		tested.distributeJobs(time);
		assertEquals(1, distributedWaitingJobs.size());
		assertEquals(0, waitingQueue.size());
	}

	@Test
	public void testDistributeJobsReturnsWaitingQueueSizeBeforeIteration() throws Exception
	{
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		int time = 7;
		Job job = Job.builder(100).submitTime(time).build();
		waitingQueue.add(job);
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue();
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(0), mock(CyclicHostSelector.class), distributedWaitingJobs, new ForTestingDistributedSimbaConfiguration());
		assertEquals(1, tested.distributeJobs(time));
	}

	@Test(expected = AssertionError.class)
	public void testOneJobNoHosts() throws Exception
	{
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).build();
		waitingQueue.add(job);
		List<HostScheduler> hostSchedulers = newArrayList();
		HostSelector hostSelector = mock(CyclicHostSelector.class);
		DistributedScheduler tested = createScheduler(waitingQueue, hostSchedulers, hostSelector);
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
		waitingQueue.add(Job.builder(100).build());
		HostScheduler h = mock(HostScheduler.class);
		when(h.host()).thenReturn(Host.builder().build());
		LinkedListWaitingQueue waitingQueueForHost = new LinkedListWaitingQueue();
		when(h.waitingJobs()).thenReturn(waitingQueueForHost);
		waitingQueueForHost.add(job);
		List<HostScheduler> hostSchedulers = newArrayList(h);
		CyclicHostSelector hostSelector = mock(CyclicHostSelector.class);
		DistributedScheduler tested = createScheduler(waitingQueue, hostSchedulers, hostSelector);
		when(hostSelector.select(any(Job.class))).thenReturn(h);
		when(h.schedule(time)).thenReturn(1);
		assertEquals(1, tested.schedule(time));
		assertEquals(0, waitingQueue.size());
		verify(hostSelector).select(job);
	}

	private DistributedScheduler createScheduler(LinkedListWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector)
	{
		return createScheduler(waitingQueue, hostSchedulers, hostSelector, new SetWaitingQueue(), new ForTestingDistributedSimbaConfiguration());
	}

	private DistributedScheduler createScheduler(LinkedListWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector, SetWaitingQueue distributedWaitingJobs, ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration)
	{
		return new DistributedScheduler(waitingQueue, hostSchedulers, hostSelector, distributedWaitingJobs, forTestingDistributedSimbaConfiguration, new PowerExpandingStrategy(forTestingDistributedSimbaConfiguration), costStatistics);
	}

	@Test
	public void testVerifyCost() throws Exception
	{
		DistributedScheduler tested = createScheduler(new LinkedListWaitingQueue(), Lists.<HostScheduler> newArrayList(), mock(HostSelector.class));
		tested.schedule(20);
		verify(costStatistics).calculate();
	}

	@Test
	public void testVirusJobShouldWaitOn1Hosts() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration = new ForTestingDistributedSimbaConfiguration();
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(15), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		assertEquals(1, tested.distributeJobs(time + forTestingDistributedSimbaConfiguration.virusTime()));
	}

	@Test
	public void testVirusJobShouldWaitOn1Hosts1() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration = new ForTestingDistributedSimbaConfiguration();
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(15), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		assertEquals(1, tested.distributeJobs(10 + forTestingDistributedSimbaConfiguration.virusTime()));
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
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(15), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		assertEquals(0, tested.distributeJobs(20));
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
	public void testShouldDuplicateJobsInWaiting() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		waitingQueue.add(job);
		final int dispatchFactor = 3;
		ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration = new ForTestingDistributedSimbaConfiguration()
		{
			@Override
			public int intialDispatchFactor()
			{
				return dispatchFactor;
			}
		};
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(5), mock(CyclicHostSelector.class), new SetWaitingQueue(), forTestingDistributedSimbaConfiguration);
		assertEquals(dispatchFactor, tested.distributeJobs(time));
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
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(pow + 5), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		assertEquals(pow, tested.distributeJobs(time + 4 * forTestingDistributedSimbaConfiguration.virusTime()));
	}

	@Test
	public void testVirusJobShouldNotWaitOnMoreThanConfiguredHostsWaitOn8Hosts() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration = new ForTestingDistributedSimbaConfiguration();
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(0), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		tested.schedule(time + 4 * forTestingDistributedSimbaConfiguration.virusTime());
		assertEquals(0, waitingQueue.size());
	}

	@Test
	public void testVirusJobShouldNotWaitOnMoreThanConfiguredHostsWaitOn8HostsTwice() throws Exception
	{
		int time = 7;
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = Job.builder(100).submitTime(time).build();
		SetWaitingQueue distributedWaitingJobs = createDitributedWaitingQueue(job);
		ForTestingDistributedSimbaConfiguration forTestingDistributedSimbaConfiguration = new ForTestingDistributedSimbaConfiguration();
		DistributedScheduler tested = createScheduler(waitingQueue, createHostSchedulers(2), mock(CyclicHostSelector.class), distributedWaitingJobs, forTestingDistributedSimbaConfiguration);
		tested.distributeJobs(time + 3 * forTestingDistributedSimbaConfiguration.virusTime());
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
