package sim.scheduling.graders;

import sim.model.GradeableHost;
import sim.model.Job;

public class AvailableMemoryGrader implements Grader
{

	@Override
	public double getGrade(GradeableHost host, Job job)
	{
		return host.availableMemory();
	}

	@Override
	public String toString()
	{
		return "worse-fit";
	}
}
