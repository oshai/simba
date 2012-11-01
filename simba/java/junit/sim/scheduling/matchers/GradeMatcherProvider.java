package sim.scheduling.matchers;

import static com.google.common.collect.Lists.*;
import sim.scheduling.graders.AvailableMemoryGrader;
import sim.scheduling.graders.ConfiguredMemoryGrader;
import sim.scheduling.graders.Grader;
import sim.scheduling.graders.Invert;
import sim.scheduling.graders.MixDegreeDeltaGrader;
import sim.scheduling.graders.MixDistanceGrader;
import sim.scheduling.graders.MixNormilizedDegreeDeltaGrader;
import sim.scheduling.graders.MixNormilizedDegreeFromTopViewDeltaGrader;

public class GradeMatcherProvider
{
	public static GradeMatcher createGraderMf1()
	{
		return createMf(new MixDegreeDeltaGrader());
	}

	private static GradeMatcher createMf(Grader grader)
	{
		return new GradeMatcher(newArrayList(invert(grader), invert(new AvailableMemoryGrader())));
	}

	public static Matcher createGraderMf2()
	{
		return createMf(new MixNormilizedDegreeDeltaGrader());
	}

	public static Matcher createGraderMf3()
	{
		return createMf(new MixDistanceGrader());
	}

	public static Matcher createGraderMf4()
	{
		return createMf(new MixNormilizedDegreeFromTopViewDeltaGrader());
	}

	public static Matcher createGraderMf5()
	{
		return new GradeMatcher(newArrayList(invert(new ConfiguredMemoryGrader()), invert(new MixDegreeDeltaGrader()), invert(new AvailableMemoryGrader())));
	}

	public static GradeMatcher createProductionGrader()
	{
		return new GradeMatcher(newArrayList(invert(new ConfiguredMemoryGrader()), invert(new AvailableMemoryGrader())));
	}

	private static Grader invert(Grader grader)
	{
		return new Invert(grader);
	}

}
