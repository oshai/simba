package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public class Invert implements Grader
{

	private final Grader grader;

	public Invert(Grader grader)
	{
		this.grader = grader;
	}

	@Override
	public double getGrade(Host host, Job job)
	{
		return -grader.getGrade(host, job);
	}

}
