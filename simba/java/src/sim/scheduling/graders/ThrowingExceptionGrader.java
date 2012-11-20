package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public class ThrowingExceptionGrader implements Grader
{

	@Override
	public double getGrade(Host host, Job job)
	{
		throw new UnsupportedOperationException("should not be called");
	}
}
