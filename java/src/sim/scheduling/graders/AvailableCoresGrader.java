package sim.scheduling.graders;

import sim.model.GradeableHost;
import sim.model.Job;

public class AvailableCoresGrader implements Grader
{

	@Override
	public double getGrade(GradeableHost host, Job job)
	{
		return host.availableCores();
	}

	@Override
	public String toString()
	{
		return "worse-fit-cores";
	}
}
