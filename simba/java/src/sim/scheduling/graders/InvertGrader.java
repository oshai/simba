package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public class InvertGrader implements Grader
{

	private final Grader grader;
	private final String name;

	public InvertGrader(Grader grader)
	{
		this(grader, "invert(" + grader.getClass().getSimpleName() + ")");
	}

	public InvertGrader(Grader grader, String name)
	{
		this.grader = grader;
		this.name = name;
	}

	@Override
	public double getGrade(Host host, Job job)
	{
		return -grader.getGrade(host, job);
	}

	@Override
	public String toString()
	{
		return name;
	}

}
