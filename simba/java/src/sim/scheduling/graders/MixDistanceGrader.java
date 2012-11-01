package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public class MixDistanceGrader implements Grader
{

	private static final double CONST = Math.sqrt(2);

	@Override
	public double getGrade(Host host, Job job)
	{
		double x0 = (host.availableCores() + job.cores()) / host.cores();
		double y0 = (host.availableMemory() + job.memory()) / host.memory();
		return Math.abs(x0 - y0) / CONST;
		// double usageRatio = ((host.usedMemory() + job.memory()) /
		// host.memory()) / ((host.usedCores() + job.cores()) / host.cores());
		// // double hostRatio = 1;// host.memory() / host.cores();
		// double hostRationTan = CONST;// Math.atan(hostRatio);
		// return Math.abs(hostRationTan - Math.atan(usageRatio));
	}

}
