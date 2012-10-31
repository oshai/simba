package sim.scheduling.graders;

import sim.model.Host;
import sim.model.Job;

public interface Grader
{
	/**
	 * @return a grade for a match, the higher the better
	 */
	public double getGrade(Host host, Job job);
}
