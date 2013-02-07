package sim.configuration;

import sim.scheduling.graders.AvailableCoresGrader;
import sim.scheduling.graders.Grader;

public class WorseFitCoresConfiguration extends ProductionSimbaConfiguration
{
	@Override
	protected Grader grader()
	{
		return new AvailableCoresGrader();
	}
}
