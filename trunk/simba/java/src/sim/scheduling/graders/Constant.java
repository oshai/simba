package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public class Constant implements Grader
{

	private final double grade;

	public Constant(double grade)
	{
		this.grade = grade;
	}

	@Override
	public double getGrade(Host host, Job job)
	{
		return grade;
	}

	@Override
	public String toString()
	{
		return "first-fit";
	}
}
