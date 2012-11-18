package sim.scheduling.graders;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;

public class MixNormilizedDegreeFromTopLeftViewDeltaGraderTest
{

	@Test
	public void testMaxCores()
	{
		Host host = Host.create().cores(1).memory(1).build();
		Job job = Job.create(1).cores(1).memory(0).build();
		Grader grader = createGrader();
		assertTrue(0 < grader.getGrade(host, job));
	}

	@Test
	public void testMaxMemory()
	{
		Host host = Host.create().cores(1).memory(1).build();
		Job job = Job.create(1).cores(0).memory(1).build();
		Grader grader = createGrader();
		assertTrue(0 < grader.getGrade(host, job));
	}

	private Grader createGrader()
	{
		return new MixNormilizedDegreeFromTopLeftViewDeltaGrader();
	}

}
