package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;
import utils.GlobalUtils;

public class SimpleMixGrader implements Grader
{

	private final Invert invertAvailableCoreGrader = new Invert(new AvailableCoresGrader());
	private final Invert invertAvailableMemoryGrader = new Invert(new AvailableMemoryGrader());

	public SimpleMixGrader()
	{
	}

	@Override
	public double getGrade(Host host, Job job)
	{
		double ratio = job.memory() / job.cores();
		if (GlobalUtils.equals(ratio, 8) || ratio > 8)
		{
			return invertAvailableCoreGrader.getGrade(host, job);
		}
		else
		{
			return invertAvailableMemoryGrader.getGrade(host, job);
		}
	}

}
