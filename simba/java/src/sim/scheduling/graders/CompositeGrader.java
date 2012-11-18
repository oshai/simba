package sim.scheduling.graders;

import java.util.List;

import sim.model.Host;
import sim.model.Job;

public class CompositeGrader implements Grader
{

	private final List<Grader> graders;
	private final int maxValue;

	public CompositeGrader(List<Grader> graders, int maxValue)
	{
		this.graders = graders;
		this.maxValue = maxValue;
	}

	@Override
	public double getGrade(Host host, Job job)
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
		return "Composite [graders=" + graders + "]";
	}

}
