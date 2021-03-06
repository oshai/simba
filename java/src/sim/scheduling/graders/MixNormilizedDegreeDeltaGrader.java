package sim.scheduling.graders;

import sim.model.GradeableHost;
import sim.model.Job;

public class MixNormilizedDegreeDeltaGrader implements Grader
{

	private static final double CONST = Math.atan(1);

	@Override
	public double getGrade(GradeableHost host, Job job)
	{
		double usageRatio = ((host.usedMemory() + job.memory()) / host.memory()) / ((host.usedCores() + job.cores()) / host.cores());
		double hostRationTan = CONST;
		return Math.abs(hostRationTan - Math.atan(usageRatio));
	}

}
