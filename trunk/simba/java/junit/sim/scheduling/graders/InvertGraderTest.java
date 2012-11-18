package sim.scheduling.graders;

import static org.junit.Assert.*;

import org.junit.Test;

public class InvertGraderTest
{

	@Test
	public void test()
	{
		assertEquals(-8.0, new InvertGrader(new Constant(8.0)).getGrade(null, null), 0.1);
	}

	@Test
	public void testToString()
	{
		new InvertGrader(new Constant(8.0)).toString();
	}

}
