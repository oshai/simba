package sim.scheduling.waiting_queue;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Job;
import sim.scheduling.waiting_queue.JobLessMemoryComparator;

public class JobLessMemoryComparatorTest
{

	@Test
	public void test()
	{
		Job job1 = Job.builder(1).memory(1.0).build();
		Job job2 = Job.builder(1).memory(2.0).build();
		Job job3 = Job.builder(1).memory(2.0).build();
		JobLessMemoryComparator tested = new JobLessMemoryComparator();
		assertEquals(-1, tested.compare(job1, job2));
		assertEquals(1, tested.compare(job2, job1));
		assertEquals(0, tested.compare(job2, job3));
	}

}
