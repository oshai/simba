package sim.scheduling.graders;

import org.junit.Test;

public class ThrowingExceptionGraderTest
{

	@Test(expected = UnsupportedOperationException.class)
	public void test()
	{
		new ThrowingExceptionGrader().getGrade(null, null);
	}

}
