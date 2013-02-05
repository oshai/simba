package sim.scheduling.graders;

import sim.model.GradeableHost;
import sim.model.Job;

public class MixNormilizedDegreeFromTopLeftViewDeltaGrader implements Grader
{

	@Override
	public double getGrade(GradeableHost host, Job job)
	{
		double normalizedAvailableMemory = (host.memory() - (host.usedMemory() + job.memory())) / host.memory();
		double normalizedAvailableCores = (host.cores() - (host.usedCores() + job.cores())) / host.cores();
		double gap = 0.1;
		return Math.atan((normalizedAvailableMemory + gap) / normalizedAvailableCores);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
