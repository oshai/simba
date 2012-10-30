package sim.scheduling;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Job;

public class JobPriorityComparatorTest
{
	
	@Test
	public void testJobPriorityComparator()
	{
		assertEquals(1, new JobPriorityComparator().compare(createJob(1), createJob(0)));
		assertEquals(-1, new JobPriorityComparator().compare(createJob(0), createJob(1)));
		assertEquals(0, new JobPriorityComparator().compare(createJob(0), createJob(0)));
	}
	
	private Job createJob(long priority)
	{
		return Job.create((long) 1).priority(priority).submitTime(0).cores(0).memory(0).build();
	}
	
	@Test
	public void testEventComparatorNoOverflow()
	{
		assertEquals(1, new JobPriorityComparator().compare(createJob(Long.MAX_VALUE), createJob(1)));
	}
	
}
