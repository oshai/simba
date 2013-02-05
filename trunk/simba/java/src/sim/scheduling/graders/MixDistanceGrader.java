package sim.scheduling.graders;

import sim.model.GradeableHost;
import sim.model.Job;

public class MixDistanceGrader implements Grader
{

	private static final double CONST = Math.sqrt(2);

	@Override
	public double getGrade(GradeableHost host, Job job)
	{
		double x0 = (host.availableCores() + job.cores()) / host.cores();
		double y0 = (host.availableMemory() + job.memory()) / host.memory();
		return Math.abs(x0 - y0) / CONST;
	}

}
