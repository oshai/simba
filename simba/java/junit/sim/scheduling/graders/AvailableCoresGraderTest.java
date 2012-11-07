package sim.scheduling.graders;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.model.Host;

public class AvailableCoresGraderTest
{

	@Test
	public void test()
	{
		Host host = mock(Host.class);
		when(host.availableCores()).thenReturn(8.0);
		assertEquals(8.0, new AvailableCoresGrader().getGrade(host, null), 0.1);
	}

}
