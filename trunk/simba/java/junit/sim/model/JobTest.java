package sim.model;

import static org.junit.Assert.assertEquals;

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
		Job job = Job.Builder.create(2).priority(7).submitTime(1).cores(3.1).memory(4.2).id("id").build();
		assertEquals(1, job.submitTime());
		assertEquals(2, job.length());
		assertEquals(3.1, job.cores(), 0.01);
		assertEquals(4.2, job.memory(), 0.01);
		assertEquals(7, job.priority());
		assertEquals("id", job.id());
		job.started(8);
		assertEquals(8, job.startTime());
	}

	@Test
	public void testToString()
	{
		Job job = Job.Builder.create(2).submitTime(1).cores(3.1).memory(4.2).id("id").build();
		assertEquals("Job [id=id, priority=0, submitTime=1, length=2, cores=3.1, memory=4.2, startTime=0]", job.toString());
	}
}
