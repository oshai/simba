package sim.scheduling.graders;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;

public class SimpleMixGraderTest
{

	@Test
	public void testNarrowJob()
	{
		Host host = Host.builder().cores(1).memory(8).build();
		Job job = Job.builder(1).cores(1).memory(1).build();
		Grader grader = createGrader();
		assertEquals(-8.0, grader.getGrade(host, job), 0.1);
	}

	@Test
	public void testWideJob()
	{
		Host host = Host.builder().cores(1).memory(8).build();
		Job job = Job.builder(1).cores(1).memory(8).build();
		Grader grader = createGrader();
		assertEquals(8.0, grader.getGrade(host, job), 0.1);
	}
	
	@Test
	public void testToString()
	{
		assertEquals("simple-mix-fit", createGrader().toString());
	}

	private Grader createGrader()
	{
		return new SimpleMixGrader();
	}

}
