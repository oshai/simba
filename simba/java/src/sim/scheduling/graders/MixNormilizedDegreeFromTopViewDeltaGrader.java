package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public class MixNormilizedDegreeFromTopViewDeltaGrader implements Grader
{

	private static final double CONST = Math.atan(1);

	@Override
	public double getGrade(Host host, Job job)
	{
		double usageRatio = ((host.memory() - (host.usedMemory() + job.memory())) / host.memory())
				/ ((host.cores() - (host.usedCores() + job.cores())) / host.cores());
		// double hostRatio = 1;// host.memory() / host.cores();
		double hostRationTan = CONST;// Math.atan(hostRatio);
		return Math.abs(hostRationTan - Math.atan(usageRatio));
	}

}
