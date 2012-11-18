package sim.scheduling.matchers;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.graders.Grader;

public class DegreeMixFitTest
{

	@Test
	public void testPerfectFit()
	{
		Host host = Host.create().cores(1).memory(1).build();
		Job job = Job.create(1).cores(1).memory(1).build();
		Grader grader = GradeMatcherProvider.createGraderMf1();
		assertEquals(0, grader.getGrade(host, job), 0.1);
	}

	@Test
	public void testMainstream()
	{
		Host host = Host.create().cores(1).memory(8).build();
		Job job1 = Job.create(1).cores(1).memory(4).build();
		Job job2 = Job.create(1).cores(1).memory(2).build();
		Grader grader = GradeMatcherProvider.createGraderMf1();
		assertTrue(grader.getGrade(host, job1) > grader.getGrade(host, job2));
	}

	@Test
	public void testUsedHost()
	{
		Host host = Host.create().cores(2).memory(16).build();
		Job job1 = Job.create(1).cores(1).memory(4).build();
		Job job2 = Job.create(1).cores(1).memory(8).build();
		Job job3 = Job.create(1).cores(1).memory(12).build();
		host.dispatchJob(job1);
		Grader grader = GradeMatcherProvider.createGraderMf1();
		assertTrue(grader.getGrade(host, job3) > grader.getGrade(host, job2));
	}

}
