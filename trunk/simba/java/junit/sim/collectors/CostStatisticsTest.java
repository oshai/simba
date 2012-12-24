package sim.collectors;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.SetWaitingQueue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class CostStatisticsTest
{
	@Mock
	private Cluster cluster;

	private SetWaitingQueue waitingQueue;
	private HashMap<String, QslotConfiguration> conf;

	private CostStatistics costStatistics;

	@Before
	public void createCostStatistics() throws Exception
	{
		waitingQueue = new SetWaitingQueue();
		conf = Maps.newHashMap();
		costStatistics = new CostStatistics(cluster, conf, waitingQueue);
	}

	@Test
	public void testEmpty() throws Exception
	{
		assertEquals(Maps.newHashMap(), costStatistics.apply());
	}

	@Test
	public void testQslotWithOutRunning() throws Exception
	{
		String qslotName = "qslot";
		QslotConfiguration qslotConf = createQslot(qslotName);
		conf.put(qslotName, qslotConf);
		assertEquals(qslotName, getQslot(qslotName).name());
	}

	@Test
	public void testQslotWith1Running() throws Exception
	{
		String qslot = "qslot";
		QslotConfiguration qslotConf = createQslot(qslot);
		conf.put(qslot, qslotConf);
		HashMap<String, Qslot> newHashMap = Maps.newHashMap();
		double cost = 2.5;
		Qslot value = new Qslot(qslotConf);
		newHashMap.put(qslot, value);
		Host host = mock(Host.class);
		when(cluster.hosts()).thenReturn(Lists.newArrayList(host));
		Job j = mock(Job.class);
		when(j.cost()).thenReturn(cost);
		when(j.qslot()).thenReturn(qslot);
		when(host.jobs()).thenReturn(Lists.newArrayList(j));
		assertEquals(2.5, getQslot(qslot).cost(), 0.0);
	}

	@Test
	public void testQslotGettingNowWith1Running() throws Exception
	{
		String qslot = "qslot";
		createQslotConf(qslot, 3);
		double cost = 1;
		Host host = mock(Host.class);
		when(cluster.hosts()).thenReturn(Lists.newArrayList(host));
		Job j = mock(Job.class);
		when(j.cost()).thenReturn(cost);
		when(j.qslot()).thenReturn(qslot);
		when(host.jobs()).thenReturn(Lists.newArrayList(j));
		assertEquals(1, getQslot(qslot).gettingNow(), 0.0);
	}

	@Test
	public void test2QslotsGettingNowWith1Running() throws Exception
	{
		String qslot1 = "qslot1";
		createQslotConf(qslot1, 3);
		String qslot = "qslot";
		createQslotConf(qslot, 3);
		Host host = mock(Host.class);
		when(cluster.hosts()).thenReturn(Lists.newArrayList(host));
		Job j1 = createJob(qslot1, 1.0);
		Job j = createJob(qslot, 1.0);
		when(host.jobs()).thenReturn(Lists.newArrayList(j, j1));
		assertEquals(0.5, getQslot(qslot).gettingNow(), 0.0);
		assertEquals(0.5, getQslot(qslot1).gettingNow(), 0.0);
	}

	@Test
	public void test2QslotsGettingNowWith2Running() throws Exception
	{
		String qslot1 = "qslot1";
		createQslotConf(qslot1, 3);
		String qslot = "qslot";
		createQslotConf(qslot, 3);
		Host host = mock(Host.class);
		when(cluster.hosts()).thenReturn(Lists.newArrayList(host));
		Job j1 = createJob(qslot1, 1.0);
		Job j = createJob(qslot, 3.0);
		when(host.jobs()).thenReturn(Lists.newArrayList(j, j1));
		assertEquals(0.75, getQslot(qslot).gettingNow(), 0.0);
		assertEquals(0.25, getQslot(qslot1).gettingNow(), 0.0);
	}

	@Test
	public void test1QslotsShouldGetWithJobsInWaiting() throws Exception
	{
		String qslot = "qslot";
		createQslotConf(qslot, 3);
		waitingQueue.add(createJob(qslot, 1.0));
		Host host = mock(Host.class);
		when(cluster.hosts()).thenReturn(Lists.newArrayList(host));
		assertEquals(1, getQslot(qslot).absoluteShouldGet(), 0.0);
	}

	@Test
	public void test2QslotsShouldGetWithJobsInWaiting() throws Exception
	{
		String qslot1 = "qslot1";
		createQslotConf(qslot1, 3);
		String qslot = "qslot";
		createQslotConf(qslot, 3);
		waitingQueue.add(createJob(qslot, 1.0));
		waitingQueue.add(createJob(qslot1, 1.0));
		Host host = mock(Host.class);
		List<Job> newArrayList = newArrayList(createJob(qslot, 1.0), createJob(qslot1, 1.0));
		when(host.jobs()).thenReturn(newArrayList);
		when(cluster.hosts()).thenReturn(Lists.newArrayList(host));
		assertEquals(0.5, getQslot(qslot).absoluteShouldGet(), 0.0);
		assertEquals(0.5, getQslot(qslot1).absoluteShouldGet(), 0.0);
		assertEquals(0.0, getQslot(qslot).absoluteShouldGetDelta(), 0.0);
		assertEquals(0.0, getQslot(qslot1).absoluteShouldGetDelta(), 0.0);
	}

	@Test
	public void test1QslotsShouldGetWithJobsInWaitingNoJobsInRunning() throws Exception
	{
		String qslot = "qslot";
		createQslotConf(qslot, 3);
		waitingQueue.add(createJob(qslot, 1.0));
		Host host = mock(Host.class);
		when(cluster.hosts()).thenReturn(Lists.newArrayList(host));
		assertEquals(1.0, getQslot(qslot).absoluteShouldGet(), 0.0);
		assertEquals(1.0, getQslot(qslot).absoluteShouldGetDelta(), 0.0);
		assertEquals(1.0, getQslot(qslot).absoluteShouldGetError(), 0.0);
	}

	@Test
	public void test1QslotsShouldGetWithNoJobsInWaitingHasNoError() throws Exception
	{
		String qslot = "qslot";
		createQslotConf(qslot, 3);
		when(cluster.hosts()).thenReturn(Lists.<Host> newArrayList());
		assertEquals(1.0, getQslot(qslot).absoluteShouldGet(), 0.0);
		assertEquals(1.0, getQslot(qslot).absoluteShouldGetDelta(), 0.0);
		assertEquals(0.0, getQslot(qslot).absoluteShouldGetError(), 0.0);
	}

	@Test
	public void test2QslotsShouldGetNegativeDeltaIsNotError() throws Exception
	{
		String qslot = "qslot";
		createQslotConf(qslot, 1);
		String qslot1 = "qslot1";
		createQslotConf(qslot1, 3);
		waitingQueue.add(createJob(qslot, 1.0));
		Host host = mock(Host.class);
		ArrayList<Job> l = Lists.newArrayList(createJob(qslot, 1.0));
		when(host.jobs()).thenReturn(l);
		when(cluster.hosts()).thenReturn(Lists.<Host> newArrayList(host));
		assertEquals(0.25, getQslot(qslot).absoluteShouldGet(), 0.0);
		assertEquals(-0.75, getQslot(qslot).absoluteShouldGetDelta(), 0.0);
		assertEquals(0.0, getQslot(qslot).absoluteShouldGetError(), 0.0);
	}

	@Test
	public void test2QslotsOnlyOneHasRunningJobsRelativeIs1() throws Exception
	{
		String qslot = "qslot";
		createQslotConf(qslot, 1);
		String qslot1 = "qslot1";
		createQslotConf(qslot1, 3);
		waitingQueue.add(createJob(qslot, 1.0));
		Host host = mock(Host.class);
		ArrayList<Job> l = Lists.newArrayList(createJob(qslot, 1.0));
		when(host.jobs()).thenReturn(l);
		when(cluster.hosts()).thenReturn(Lists.<Host> newArrayList(host));
		assertEquals(1.0, getQslot(qslot).relativeRunningShouldGet(), 0.0);
		assertEquals(0.0, getQslot(qslot).relativeRunningShouldGetDelta(), 0.0);
		assertEquals(0.0, getQslot(qslot).relativeRunningShouldGetError(), 0.0);
	}

	@Test
	public void test3Qslots1DoesNotRun() throws Exception
	{
		String qslot = "qslot";
		createQslotConf(qslot, 3);
		String noWaitingJobs = "qslot1";
		createQslotConf(noWaitingJobs, 1);
		String noRunningJobs = "qslot2";
		createQslotConf(noRunningJobs, 4);
		waitingQueue.add(createJob(qslot, 1.0));
		waitingQueue.add(createJob(noRunningJobs, 1.0));
		Host host = mock(Host.class);
		ArrayList<Job> l = Lists.newArrayList(createJob(qslot, 0.5), createJob(noWaitingJobs, 0.5));
		when(host.jobs()).thenReturn(l);
		when(cluster.hosts()).thenReturn(Lists.<Host> newArrayList(host));
		assertEquals(0.5, getQslot(qslot).gettingNow(), 0.0);
		assertEquals(0.75, getQslot(qslot).relativeRunningShouldGet(), 0.0);
		assertEquals(0.25, getQslot(qslot).relativeRunningShouldGetDelta(), 0.0);
		assertEquals(0.25, getQslot(qslot).relativeRunningShouldGetError(), 0.0);
		assertEquals(-0.25, getQslot(noWaitingJobs).relativeRunningShouldGetDelta(), 0.0);
		assertEquals(0.0, getQslot(noWaitingJobs).relativeRunningShouldGetError(), 0.0);
		assertEquals(0.0, getQslot(noWaitingJobs).relativeWaitingShouldGetError(), 0.0);
		assertEquals(0.5, getQslot(noRunningJobs).absoluteShouldGet(), 0.0);
		assertEquals(0.5, getQslot(noRunningJobs).absoluteShouldGetError(), 0.0);
		assertEquals(0.0, getQslot(noRunningJobs).relativeRunningShouldGet(), 0.0);
		assertEquals(0.0, getQslot(noRunningJobs).relativeRunningShouldGetError(), 0.0);
		assertEquals(4.0 / 7.0, getQslot(noRunningJobs).relativeWaitingShouldGet(), 0.01);
		assertEquals(4.0 / 7.0, getQslot(noRunningJobs).relativeWaitingShouldGetError(), 0.01);
		assertEquals(0.5, getQslot(noRunningJobs).relativeShouldGetError(), 0.01);
	}

	@Test
	public void testRelativeRunningSHouldGetErrorForQslotWIthoutWaitingJobsThatHasError() throws Exception
	{
		String qslot = "qslot";
		createQslotConf(qslot, 1);
		String noWaitingJobs = "qslot1";
		createQslotConf(noWaitingJobs, 3);
		waitingQueue.add(createJob(qslot, 1.0));
		Host host = mock(Host.class);
		ArrayList<Job> l = Lists.newArrayList(createJob(qslot, 1), createJob(noWaitingJobs, 1));
		when(host.jobs()).thenReturn(l);
		when(cluster.hosts()).thenReturn(Lists.<Host> newArrayList(host));
		assertEquals(0.5, getQslot(noWaitingJobs).gettingNow(), 0.0);
		assertEquals(0.75, getQslot(noWaitingJobs).relativeRunningShouldGet(), 0.0);
		assertEquals(0.25, getQslot(noWaitingJobs).relativeRunningShouldGetDelta(), 0.0);
		assertEquals(0.0, getQslot(noWaitingJobs).relativeRunningShouldGetError(), 0.0);
		assertEquals(0.25, getQslot(noWaitingJobs).relativeShouldGetDelta(), 0.0);
		assertEquals(0.0, getQslot(noWaitingJobs).relativeShouldGetError(), 0.0);
	}

	private Qslot getQslot(String noWaitingJobs)
	{
		return costStatistics.apply().get(noWaitingJobs);
	}

	private Job createJob(String qslot, double runningCost)
	{
		Job j = mock(Job.class);
		when(j.cost()).thenReturn(runningCost);
		when(j.qslot()).thenReturn(qslot);
		return j;
	}

	private void createQslotConf(String qslot, double allocation)
	{
		QslotConfiguration qslotConf = createQslot(qslot, Double.MAX_VALUE, allocation);
		conf.put(qslot, qslotConf);
	}

	@Test
	public void testMaxCostError() throws Exception
	{
		String qslot = "qslot";
		QslotConfiguration qslotConf = createQslot(qslot, 1.0);
		conf.put(qslot, qslotConf);
		double cost = 2.5;
		Host host = mock(Host.class);
		when(cluster.hosts()).thenReturn(Lists.newArrayList(host));
		Job j = mock(Job.class);
		when(j.cost()).thenReturn(cost);
		when(j.qslot()).thenReturn(qslot);
		when(host.jobs()).thenReturn(Lists.newArrayList(j));
		assertEquals(1.5, getQslot(qslot).costError(), 0.0);
	}

	@Test
	public void testMaxCostErrorNegative() throws Exception
	{
		String qslot = "qslot";
		QslotConfiguration qslotConf = createQslot(qslot, 2.0);
		conf.put(qslot, qslotConf);
		double cost = 1;
		Host host = mock(Host.class);
		when(cluster.hosts()).thenReturn(Lists.newArrayList(host));
		Job j = mock(Job.class);
		when(j.cost()).thenReturn(cost);
		when(j.qslot()).thenReturn(qslot);
		when(host.jobs()).thenReturn(Lists.newArrayList(j));
		assertEquals(0, getQslot(qslot).costError(), 0.0);
	}

	@Test
	public void test2QslotsWith1Running() throws Exception
	{
		String qslot = "qslot";
		conf.put(qslot, createQslot(qslot));
		String notQslot = "notQslot";
		conf.put(notQslot, createQslot(notQslot));
		HashMap<String, Qslot> newHashMap = Maps.newHashMap();
		double cost = 2.5;
		Qslot value = new Qslot(createQslot(qslot));
		newHashMap.put(qslot, value);
		Host host = mock(Host.class);
		when(cluster.hosts()).thenReturn(Lists.newArrayList(host));
		Job j = mock(Job.class);
		when(j.cost()).thenReturn(cost);
		when(j.qslot()).thenReturn("qslot");
		Job j2 = mock(Job.class);
		when(j2.cost()).thenReturn(cost);
		when(j2.qslot()).thenReturn(notQslot);
		when(j.cost()).thenReturn(cost);
		when(host.jobs()).thenReturn(Lists.newArrayList(j, j2));
		assertEquals(2.5, getQslot(qslot).cost(), 0.0);
	}

	private QslotConfiguration createQslot(String qslot)
	{
		return createQslot(qslot, 0.0);
	}

	private QslotConfiguration createQslot(String qslot, double maxCost)
	{
		return createQslot(qslot, maxCost, 1);
	}

	private QslotConfiguration createQslot(String qslot, double maxCost, double allocation)
	{
		return new QslotConfiguration(qslot, maxCost, allocation);
	}

	@Test
	public void testQslotMaxCost() throws Exception
	{
		String qslotName = "qslot";
		double maxCost = 15.4;
		QslotConfiguration qslotConf = createQslot(qslotName, maxCost);
		conf.put(qslotName, qslotConf);
		assertEquals(maxCost, getQslot(qslotName).maxCost(), 0.0);
	}
}
