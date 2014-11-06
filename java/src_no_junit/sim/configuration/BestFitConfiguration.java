package sim.configuration;

import sim.scheduling.graders.Grader;
import sim.scheduling.matchers.GradeMatcherProvider;

public class BestFitConfiguration extends ProductionSimbaConfiguration
{
	@Override
	protected Grader grader()
	{
		return GradeMatcherProvider.createGraderBf2();
	}
}
