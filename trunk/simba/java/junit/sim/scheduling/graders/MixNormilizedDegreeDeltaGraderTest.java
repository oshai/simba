package sim.scheduling.graders;

import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;

public class MixNormilizedDegreeDeltaGraderTest
{

	@Test
	public void test()
	{
		new MixNormilizedDegreeDeltaGrader().getGrade(mock(Host.class), mock(Job.class));
	}

}
