package sim.scheduling.graders;

import sim.model.GradeableHost;
import sim.model.Job;

public interface Grader
{
	/**
	 * @return a grade for a match, the higher the better
	 */
	public double getGrade(GradeableHost host, Job job);
}
