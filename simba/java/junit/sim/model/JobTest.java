package sim.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class JobTest
{

	@Test
	public void testWaitTime()
	{
		Job job = Job.builder(1).submitTime(1).build();
		job.started(5);
		assertEquals(4, job.waitTime());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeLength()
	{
		Job.builder(0).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTooLongLength()
	{
		Job.builder(Long.MAX_VALUE).build();
	}

	@Test
	public void testMembers()
	{
		Job job = Job.builder(2).priority(7).cost(2.4).submitTime(1).cores(3.1).memory(4.2).id("id").build();
		assertEquals(1, job.submitTime());
		assertEquals(2, job.length());
		assertEquals(3.1, job.cores(), 0.01);
		assertEquals(4.2, job.memory(), 0.01);
		assertEquals(2.4, job.cost(), 0.01);
		assertEquals(7, job.priority());
		assertEquals("id", job.id());
		job.started(8);
		assertEquals(8, job.startTime());
	}

	@Test
	public void testToString()
	{
		Job job = Job.builder(2).submitTime(1).cores(3.1).memory(4.2).id("id").build();
		assertEquals("Job [id=id, priority=0, submitTime=1, length=2, cores=3.1, memory=4.2, startTime=0, cost=0.0]", job.toString());
	}
}
