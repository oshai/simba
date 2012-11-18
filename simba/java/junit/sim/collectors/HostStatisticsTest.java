package sim.collectors;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.WaitingQueue;

public class HostStatisticsTest
{

	@Test(expected = IllegalArgumentException.class)
	public void testEmpty()
	{
		create(new Cluster());
	}

	@Test
	public void testEmpty2()
	{
		Cluster c = new Cluster();
		c.add(Host.create().build());
		HostStatistics tested = create(c);
		assertEquals(0, tested.cores());
		assertEquals(0, tested.memory());
		assertEquals(0, tested.usedMemoryAverage());
		assertEquals(0, tested.usedMemoryVariance(), 0.1);
		assertEquals(0, tested.mixAverage());
		assertEquals(0, tested.mixVariance(), 0.1);
		assertEquals(0, tested.usedCores());
		assertEquals(0, tested.usedMemory());
		assertEquals(0, tested.waitingJobs());
	}

	@Test
	public void testSimple()
	{
		Cluster c = new Cluster();
		Host host = Host.create().cores(2).memory(2).build();
		host.dispatchJob(Job.create(1).cores(1).memory(1).build());
		c.add(host);
		HostStatistics tested = create(c);
		assertEquals(2, tested.cores());
		assertEquals(2, tested.memory());
		assertEquals(1, tested.usedMemoryAverage());
		assertEquals(0, tested.usedMemoryVariance(), 0.1);
		assertEquals(1, tested.mixAverage());
		assertEquals(0, tested.mixVariance(), 0.1);
		assertEquals(1, tested.usedCores());
		assertEquals(1, tested.usedMemory());
	}

	public HostStatistics create(Cluster c)
	{
		WaitingQueue w = new WaitingQueue();
		HostStatistics tested = new HostStatistics(c, w);
		return tested;
	}

}
