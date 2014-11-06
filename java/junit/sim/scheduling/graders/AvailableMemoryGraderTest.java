package sim.scheduling.graders;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.model.Host;

public class AvailableMemoryGraderTest
{

	@Test
	public void test()
	{
		Host host = mock(Host.class);
		when(host.availableMemory()).thenReturn(8.0);
		assertEquals(8.0, new AvailableMemoryGrader().getGrade(host, null), 0.1);
	}

	@Test
	public void testToString()
	{
		assertEquals("worse-fit", new AvailableMemoryGrader().toString());
	}
}
