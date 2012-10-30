package sim.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class JobTest
{
	
	@Test
	public void testWaitTime()
	{
		Job job = Job.Builder.create(1).submitTime(1).build();
		job.started(5);
		assertEquals(4, job.waitTime());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNegativeLength()
	{
		Job.Builder.create(0).build();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTooLongLength()
	{
		Job.Builder.create(Long.MAX_VALUE).build();
	}
	
	@Test
	public void testMembers()
	{
		Job job = Job.Builder.create(2).submitTime(1).cores(3.1).memory(4.2).id("id").build();
		assertEquals(1, job.submitTime());
		assertEquals(2, job.length());
		assertEquals(3.1, job.cores(), 0.01);
		assertEquals(4.2, job.memory(), 0.01);
		assertEquals("id", job.id());
	}
}
