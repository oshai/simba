package sim.model;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class HostTest
{

	@Test
	public void testDispatchAndFinish()
	{
		Host host = Host.create().cores(0).memory(0).build();
		Job job = Job.create(1).build();
		host.dispatchJob(job);
		assertEquals(newArrayList(job), host.jobs());
		host.finishJob(job);
		assertEquals(newArrayList(), host.jobs());
	}

	@Test
	public void test_hasAvailableResourcesFor_false()
	{
		Host host = Host.create().cores(0).memory(0).build();
		Job job = Job.create(1).cores(1).build();
		assertFalse(host.hasAvailableResourcesFor(job));
	}

	@Test
	public void test_hasAvailableResourcesFor_core_true()
	{
		Host host = Host.create().cores(1).memory(0).build();
		Job job = Job.create(1).cores(1).build();
		assertTrue(host.hasAvailableResourcesFor(job));
	}

	@Test
	public void test_hasAvailableResourcesFor_memory_false()
	{
		Host host = Host.create().cores(0).memory(0).build();
		Job job = Job.create(1).memory(1).build();
		assertFalse(host.hasAvailableResourcesFor(job));
	}

	@Test
	public void test_hasAvailableResourcesFor_core_with_jobs()
	{
		Host host = Host.create().cores(1).memory(0).build();
		Job job = Job.create(1).cores(1).build();
		assertTrue(host.hasAvailableResourcesFor(job));
		host.dispatchJob(job);
		assertFalse(host.hasAvailableResourcesFor(job));
	}

	@Test
	public void test_hasAvailableResourcesFor_memory_with_jobs()
	{
		Host host = Host.create().cores(0).memory(1).build();
		Job job = Job.create(1).memory(1).build();
		assertTrue(host.hasAvailableResourcesFor(job));
		host.dispatchJob(job);
		assertFalse(host.hasAvailableResourcesFor(job));
	}

	@Test
	public void testMembers()
	{
		Host host = Host.create().cores(0.1).memory(1.1).id("id").build();
		assertEquals(0.1, host.cores(), 0.01);
		assertEquals(1.1, host.memory(), 0.01);
		assertEquals("id", host.id());
	}

	@Test
	public void testToString()
	{
		Host host = Host.create().cores(0.1).memory(1.1).id("id").build();
		assertEquals("Host [id=id, cores=0.1, memory=1.1, jobs=[]]", host.toString());
	}
}
