package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public class ConfiguredMemoryGrader implements Grader
{

	@Override
	public double getGrade(Host host, Job job)
	{
		return host.memory();
	}

}
