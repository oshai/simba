package sim.scheduling.best_fit;

import static org.junit.Assert.*;

import org.junit.Test;

import sim.model.Host;

public class HostsComperatorTest
{
	
	@Test
	public void test()
	{
		assertFalse(0 == new HostsComperator().compare(Host.Builder.create().build(), Host.Builder.create().build()));
	}
	
}
