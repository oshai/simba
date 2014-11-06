package sim;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClockTest
{
	@Test
	public void test() throws Exception
	{
		Clock tested = new Clock();
		tested.time(1);
		assertEquals(1, tested.time());
	}
}
