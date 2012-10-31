package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public class MixDegreeDeltaGrader implements Grader
{

	@Override
	public double getGrade(Host host, Job job)
	{
		double hostRatio = host.memory() / host.cores();
		double usageRatio = (host.usedMemory() + job.memory()) / (host.usedCores() + job.cores());
		return Math.abs(Math.atan(hostRatio) - Math.atan(usageRatio));
	}

}
