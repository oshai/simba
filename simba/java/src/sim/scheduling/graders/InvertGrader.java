package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public class InvertGrader implements Grader
{

	private final Grader grader;

	public InvertGrader(Grader grader)
	{
		this.grader = grader;
	}

	@Override
	public double getGrade(Host host, Job job)
	{
		return -grader.getGrade(host, job);
	}

	@Override
	public String toString()
	{
		return "InvertGrader [grader=" + grader.getClass().getSimpleName() + "]";
	}

}
