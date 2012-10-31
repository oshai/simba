package sim.scheduling.matchers;

import static com.google.common.collect.Lists.*;
import sim.scheduling.graders.AvailableMemoryGrader;
import sim.scheduling.graders.ConfiguredMemoryGrader;
import sim.scheduling.graders.Grader;
import sim.scheduling.graders.Invert;
import sim.scheduling.graders.MixDegreeDeltaGrader;

public class GradeMatcherProvider
{
	public static GradeMatcher createGraderMf1()
	{
		return new GradeMatcher(newArrayList(invert(new MixDegreeDeltaGrader()), invert(new AvailableMemoryGrader())));
	}

	public static GradeMatcher createGraderProduction()
	{
		return new GradeMatcher(newArrayList(invert(new ConfiguredMemoryGrader()), invert(new AvailableMemoryGrader())));
	}

	private static Grader invert(Grader grader)
	{
		return new Invert(grader);
	}
}
