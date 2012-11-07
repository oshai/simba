package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public class AvailableCoresGrader implements Grader
{

	@Override
	public double getGrade(Host host, Job job)
	{
		return host.availableCores();
	}

}
