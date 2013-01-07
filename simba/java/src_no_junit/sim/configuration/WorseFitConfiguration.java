package sim.configuration;

import sim.scheduling.graders.AvailableMemoryGrader;
import sim.scheduling.graders.Grader;

public class WorseFitConfiguration extends ProductionSimbaConfiguration
{
	@Override
	protected Grader getGrader()
	{
		return new AvailableMemoryGrader();
	}
}
