package sim.scheduling.graders;

import sim.model.GradeableHost;
import sim.model.Job;

public class ThrowingExceptionGrader implements Grader
{

	@Override
	public double getGrade(GradeableHost host, Job job)
	{
		throw new UnsupportedOperationException("should not be called");
	}

	@Override
	public String toString()
	{
		return "ThrowingExceptionGrader";
	}

}
