package sim.configuration;

import sim.scheduling.graders.Constant;
import sim.scheduling.graders.Grader;

public class FirstFitConfiguration extends ProductionSimbaConfiguration
{
	@Override
	protected Grader grader()
	{
		return new Constant(1.0);
	}
}
