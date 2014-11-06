package sim.scheduling.graders;

import sim.model.GradeableHost;
import sim.model.Job;

public class ConfiguredMemoryGrader implements Grader
{

	@Override
	public double getGrade(GradeableHost host, Job job)
	{
		return host.memory();
	}

}
