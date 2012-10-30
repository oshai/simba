package sim.scheduling.matchers;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;

public class RandomFitTest
{
	@Test
	public void testNoAccpetingHostNotFit()
	{
		Host host = Host.create().cores(1).build();
		Host host2 = Host.create().cores(1).build();
		Host host3 = Host.create().cores(1).build();
		List<Host> hosts = newArrayList(host, host2, host3);
		Random random = mock(Random.class);
		when(random.nextInt(3)).thenReturn(1);
		assertEquals(null, new RandomFit(random).match(Job.create((long) 1).cores(2).build(), hosts));
	}

	@Test
	public void testAccpetingHostFit()
	{
		Host host = Host.create().cores(1).build();
		List<Host> hosts = newArrayList(host);
		assertEquals(host, new RandomFit().match(Job.create((long) 1).cores(1).build(), hosts));
	}

	@Test
	public void testAccpetingRandomHost()
	{
		Host host1 = Host.create().cores(2).memory(16).build();
		Host host2 = Host.create().cores(2).memory(15).build();
		Host host3 = Host.create().cores(2).memory(8).build();
		List<Host> hosts = newArrayList(host1, host2, host3);
		Random random = mock(Random.class);
		when(random.nextInt(3)).thenReturn(1);
		assertEquals(host2, new RandomFit(random).match(Job.create((long) 1).cores(1).memory(4).build(), hosts));
	}

	@Test
	public void testAccpetingNextHostIfNotAvailable()
	{
		Host host1 = Host.create().cores(2).memory(16).build();
		Host host2 = Host.create().cores(2).memory(3).build();
		Host host3 = Host.create().cores(2).memory(8).build();
		List<Host> hosts = newArrayList(host1, host2, host3);
		Random random = mock(Random.class);
		when(random.nextInt(3)).thenReturn(1);
		assertEquals(host3, new RandomFit(random).match(Job.create((long) 1).cores(1).memory(4).build(), hosts));
	}

	@Test
	public void testAccpetingNextHostIfNotAvailableTheReturnToListStart()
	{
		Host host1 = Host.create().cores(2).memory(16).build();
		Host host2 = Host.create().cores(2).memory(3).build();
		Host host3 = Host.create().cores(2).memory(3).build();
		List<Host> hosts = newArrayList(host1, host2, host3);
		Random random = mock(Random.class);
		when(random.nextInt(3)).thenReturn(1);
		assertEquals(host1, new RandomFit(random).match(Job.create((long) 1).cores(1).memory(4).build(), hosts));
	}

}
