package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;
import utils.GlobalUtils;

public class SimpleMixGrader implements Grader
{

	private final Grader availableMemoryGrader = new AvailableMemoryGrader();
	private final InvertGrader invertAvailableMemoryGrader = new InvertGrader(new AvailableMemoryGrader());

	public SimpleMixGrader()
	{
	}

	@Override
	public double getGrade(Host host, Job job)
	{
		double ratio = job.memory() / job.cores();
		if (GlobalUtils.equals(ratio, 8) || ratio > 8)
		{
			return availableMemoryGrader.getGrade(host, job);
		}
		else
		{
			return invertAvailableMemoryGrader.getGrade(host, job);
		}
	}

}
