package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;
import utils.GlobalUtils;

public class SimpleMixGrader implements Grader
{

	private static final int RATIO = 4;
	private final Grader availableMemoryGrader = new AvailableMemoryGrader();
	private final InvertGrader invertAvailableMemoryGrader = new InvertGrader(new AvailableMemoryGrader());

	public SimpleMixGrader()
	{
	}

	@Override
	public double getGrade(Host host, Job job)
	{
		double ratio = job.memory() / job.cores();
		if (GlobalUtils.equals(ratio, RATIO) || ratio > RATIO)
		{
			return availableMemoryGrader.getGrade(host, job);
		}
		else
		{
			return invertAvailableMemoryGrader.getGrade(host, job);
		}
	}

	@Override
	public String toString()
	{
		return "simple-mix-fit";
	}

}
