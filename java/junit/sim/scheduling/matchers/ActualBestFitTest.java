package sim.scheduling.matchers;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import sim.model.Host;
import sim.model.Job;

public class ActualBestFitTest
{
	@Test
	public void testNoAccpetingHostNotFit()
	{
		List<Host> hosts = newArrayList(Host.builder().build());
		assertNull(new ActualBestFit().match(Job.builder(1).cores(1).build(), hosts));
	}

	@Test
	public void testAccpetingHostFit()
	{
		Host host = Host.builder().cores(1).build();
		List<Host> hosts = newArrayList(host);
		assertEquals(host, new ActualBestFit().match(Job.builder(1).cores(1).build(), hosts));
	}

	@Test
	public void testAccpetingHostWithLessAvailableMemory()
	{
		Job job = Job.builder(1).cores(0).memory(1).build();
		Host host1 = Mockito.mock(Host.class);
		when(host1.availableMemory()).thenReturn(4.0);
		when(host1.memory()).thenReturn(8.0);
		when(host1.hasAvailableResourcesFor(job)).thenReturn(true);
		Host host2 = Mockito.mock(Host.class);
		when(host2.availableMemory()).thenReturn(2.0);
		when(host2.memory()).thenReturn(8.0);
		when(host2.hasAvailableResourcesFor(job)).thenReturn(true);
		List<Host> hosts = newArrayList(host1, host2);
		assertEquals(host2, new ActualBestFit().match(job, hosts));
	}

	@Test
	public void testAccpetingHostWithLessMemory()
	{
		Job job = Job.builder(1).cores(0).memory(1).build();
		Host host1 = Mockito.mock(Host.class);
		when(host1.availableMemory()).thenReturn(2.0);
		when(host1.memory()).thenReturn(8.0);
		when(host1.hasAvailableResourcesFor(job)).thenReturn(true);
		Host host2 = Mockito.mock(Host.class);
		when(host2.availableMemory()).thenReturn(4.0);
		when(host2.memory()).thenReturn(5.0);
		when(host2.hasAvailableResourcesFor(job)).thenReturn(true);
		List<Host> hosts = newArrayList(host1, host2);
		assertEquals(host2, new ActualBestFit().match(job, hosts));
	}

}
