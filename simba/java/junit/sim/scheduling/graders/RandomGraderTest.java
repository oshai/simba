package sim.scheduling.graders;

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomGraderTest
{

	@Test
	public void test()
	{
		assertTrue(new RandomGrader(10).getGrade(null, null) < 10);
	}

}
