package sim.scheduling.matchers;

import static com.google.common.collect.Lists.*;
import sim.scheduling.graders.AvailableCoresGrader;
import sim.scheduling.graders.AvailableMemoryGrader;
import sim.scheduling.graders.CompositeGrader;
import sim.scheduling.graders.ConfiguredMemoryGrader;
import sim.scheduling.graders.Grader;
import sim.scheduling.graders.InvertGrader;
import sim.scheduling.graders.MixDegreeDeltaGrader;
import sim.scheduling.graders.MixDistanceGrader;
import sim.scheduling.graders.MixNormilizedDegreeFromTopLeftViewDeltaGrader;
import sim.scheduling.graders.MixNormilizedDegreeFromTopViewDeltaGrader;
import sim.scheduling.graders.SimpleMixGrader;

public class GradeMatcherProvider
{
	public static Grader createGraderMf1()
	{
		return invert(new MixDegreeDeltaGrader(), "mix-fit");
	}

	private static Grader invert(Grader grader, String name)
	{
		return new InvertGrader(grader, name);
	}

	public static Grader createGraderMf3()
	{
		return invert(new MixDistanceGrader());
	}

	public static Grader createGraderMf4()
	{
		return invert(new MixNormilizedDegreeFromTopViewDeltaGrader());
	}

	public static Grader createGraderMf6()
	{
		return new MixNormilizedDegreeFromTopLeftViewDeltaGrader();
	}

	public static Grader createGraderBfi()
	{
		return new CompositeGrader(newArrayList(invert(new ConfiguredMemoryGrader()), invert(new AvailableMemoryGrader()), invert(new AvailableCoresGrader())), 1000, "best-fit-improved");
	}

	public static Grader createGraderBf2()
	{
		return invert(new AvailableMemoryGrader(), "best-fit");
	}

	public static Grader createGraderBfCores()
	{
		return invert(new AvailableCoresGrader(), "best-fit-cores");
	}

	public static Grader createProductionGrader()
	{
		return new CompositeGrader(newArrayList(invert(new ConfiguredMemoryGrader()), invert(new AvailableMemoryGrader())), 1000, "netbatch-fit");
	}

	private static Grader invert(Grader grader)
	{
		return new InvertGrader(grader);
	}

	public static Grader createGraderSmf()
	{
		return new SimpleMixGrader();
	}

}
