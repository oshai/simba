package sim.scheduling.graders;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;

public class SimpleMixFitTest
{

	@Test
	public void testNarrowJob()
	{
		Host host = Host.create().cores(1).memory(8).build();
		Job job = Job.create(1).cores(1).memory(1).build();
		Grader grader = createGrader();
		assertEquals(-8.0, grader.getGrade(host, job), 0.1);
	}

	@Test
	public void testWideJob()
	{
		Host host = Host.create().cores(1).memory(8).build();
		Job job = Job.create(1).cores(1).memory(8).build();
		Grader grader = createGrader();
		assertEquals(8.0, grader.getGrade(host, job), 0.1);
	}

	private Grader createGrader()
	{
		return new SimpleMixGrader();
	}

}
