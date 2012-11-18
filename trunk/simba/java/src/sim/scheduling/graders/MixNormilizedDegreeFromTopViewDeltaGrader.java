package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;
import utils.GlobalUtils;

public class MixNormilizedDegreeFromTopViewDeltaGrader implements Grader
{

	private static final double CONST = Math.atan(1);
	private static final double NORMALIZER = 1.01;

	@Override
	public double getGrade(Host host, Job job)
	{
		double usedCores = host.usedCores() + job.cores();
		double usedMemory = host.usedMemory() + job.memory();
		if (GlobalUtils.equals(host.memory(), usedMemory) && GlobalUtils.equals(host.cores(), usedCores))
		{
			return 0;
		}
		double availableCores = host.cores() * NORMALIZER - usedCores;
		double availableCoresNormalized = availableCores / host.cores();
		double availableMemory = host.memory() * NORMALIZER - usedMemory;
		double availableMemoryNormalized = availableMemory / host.memory();
		double usageRatio = availableCoresNormalized / availableMemoryNormalized;
		double hostRationTan = CONST;
		return Math.abs(hostRationTan - Math.atan(usageRatio));
	}

}
