package sim.collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class CostStatisticsTest
{
	@Mock
	private Cluster cluster;

	private HashMap<String, QslotConfiguration> conf = Maps.newHashMap();

	private CostStatistics costStatistics;

	@Before
	public void createCostStatistics() throws Exception
	{
		costStatistics = new CostStatistics(cluster, conf);
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
		assertEquals(qslotName, costStatistics.apply().get(qslotName).name());
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
		assertEquals(2.5, costStatistics.apply().get(qslot).cost(), 0.0);
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
		assertEquals(2.5, costStatistics.apply().get(qslot).cost(), 0.0);
	}

	private QslotConfiguration createQslot(String qslot)
	{
		return createQslot(qslot, 0.0);
	}

	private QslotConfiguration createQslot(String qslot, double maxCost)
	{
		return new QslotConfiguration(qslot, maxCost);
	}

	@Test
	public void testQslotMaxCost() throws Exception
	{
		String qslotName = "qslot";
		double maxCost = 15.4;
		QslotConfiguration qslotConf = createQslot(qslotName, maxCost);
		conf.put(qslotName, qslotConf);
		assertEquals(maxCost, costStatistics.apply().get(qslotName).maxCost(), 0.0);
	}
}
