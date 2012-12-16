package sim.distributed;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.*;

import sim.model.*;

public class CyclicHostSelectorTest
{

	@Test
	public void testEmpty()
	{
		Job j = mock(Job.class);
		List<HostScheduler> s = newArrayList();
		HostSelector tested = new CyclicHostSelector(s);
		assertNull(tested.select(j));
	}

	@Test
	public void testOneHost()
	{
		Job j = mock(Job.class);
		HostScheduler hs = mock(HostScheduler.class);
		List<HostScheduler> s = newArrayList(hs);
		when(hs.isAllowedToAddJob(j)).thenReturn(true);
		HostSelector tested = new CyclicHostSelector(s);
		assertEquals(hs, tested.select(j));
		assertEquals(hs, tested.select(j));
	}

	@Test
	public void testTwoHostsFirstCannotRun()
	{
		HostScheduler hs = mock(HostScheduler.class);
		HostScheduler hs1 = mock(HostScheduler.class);
		Job j = mock(Job.class);
		when(hs1.isAllowedToAddJob(j)).thenReturn(true);
		List<HostScheduler> s = newArrayList(hs, hs1);

		HostSelector tested = new CyclicHostSelector(s);
		assertEquals(hs1, tested.select(j));
	}

	@Test
	public void testHostCannotRun()
	{
		Job j = mock(Job.class);
		HostScheduler hs = mock(HostScheduler.class);
		List<HostScheduler> s = newArrayList(hs);
		HostSelector tested = new CyclicHostSelector(s);
		assertEquals(null, tested.select(j));
	}

}
