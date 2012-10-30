package sim.scheduling.matchers;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;

public class FirstFitTest
{
	
	@Test
	public void testNoMatch()
	{
		Job job = Job.create((long) 1).priority(0).submitTime(0).cores(0).memory(0).build();
		List<Host> hosts = newArrayList();
		Host $ = new FirstFit().match(job, hosts);
		assertNull($);
	}
	
	@Test
	public void testMatch()
	{
		Job job = Job.create((long) 1).priority(0).submitTime(0).cores(0).memory(0).build();
		List<Host> hosts = newArrayList();
		hosts.add(mock(Host.class));
		Host host2 = mock(Host.class);
		hosts.add(host2);
		when(host2.hasAvailableResourcesFor(job)).thenReturn(true);
		Host $ = new FirstFit().match(job, hosts);
		assertEquals(host2, $);
	}
	
}
