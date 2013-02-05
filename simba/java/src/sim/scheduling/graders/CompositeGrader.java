package sim.scheduling.graders;

import java.util.List;

import sim.model.GradeableHost;
import sim.model.Job;

public class CompositeGrader implements Grader
{

	private final List<Grader> graders;
	private final int maxValue;
	private final String name;

	public CompositeGrader(List<Grader> graders, int maxValue)
	{
		this(graders, maxValue, "Composite(" + graders + ")");
	}

	public CompositeGrader(List<Grader> graders, int maxValue, String name)
	{
		this.graders = graders;
		this.maxValue = maxValue;
		this.name = name;
	}

	@Override
	public double getGrade(GradeableHost host, Job job)
	{
		double $ = 0;
		for (Grader grader : graders)
		{
			$ *= maxValue;
			$ += grader.getGrade(host, job);
		}
		return $;
	}

	@Override
	public String toString()
	{
		return name;
	}

}
