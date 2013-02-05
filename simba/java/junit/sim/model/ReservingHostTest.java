package sim.model;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.scheduling.reserving.ReservingSchedulerUtils;

public class ReservingHostTest
{
	@Test
	public void testName() throws Exception
	{
		Host host = mock(Host.class);
		when(host.cores()).thenReturn(2.0);
		when(host.memory()).thenReturn(3.2);
		ReservingSchedulerUtils utils = mock(ReservingSchedulerUtils.class);
		when(utils.availableCores((Host) any())).thenReturn(1.0);
		when(utils.availableMemory((Host) any())).thenReturn(2.0);
		ReservingHost tested = new ReservingHost(host, utils);
		assertEquals(2.0, tested.cores(), 0.0);
		assertEquals(1.0, tested.availableCores(), 0.0);
		assertEquals(1.0, tested.usedCores(), 0.0);
		assertEquals(3.2, tested.memory(), 0.0);
		assertEquals(2.0, tested.availableMemory(), 0.0);
		assertEquals(1.2, tested.usedMemory(), 0.01);

	}
}
