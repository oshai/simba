package sim.scheduling.graders;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class CompositeGraderTest
{

	@Test
	public void testOneGrader()
	{
		List<Grader> l = newArrayList((Grader) new Constant(2));
		assertEquals(2.0, new CompositeGrader(l, 10).getGrade(null, null), 0.1);
	}

	@Test
	public void test2Graders()
	{
		List<Grader> l = newArrayList((Grader) new Constant(2), new Constant(1));
		assertEquals(21.0, new CompositeGrader(l, 10).getGrade(null, null), 0.1);
	}

	@Test
	public void testToString()
	{
		assertFalse(new CompositeGrader(null, 0).toString().contains("@"));
	}
}
