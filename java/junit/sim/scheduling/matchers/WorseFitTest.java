package sim.scheduling.matchers;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;

public class WorseFitTest
{
	@Test
	public void testNoAccpetingHostNotFit()
	{
		List<Host> hosts = newArrayList(Host.builder().build());
		assertNull(new WorseFit().match(Job.builder(1).cores(1).build(), hosts));
	}

	@Test
	public void testAccpetingHostFit()
	{
		Host host = Host.builder().cores(1).build();
		List<Host> hosts = newArrayList(host);
		assertEquals(host, new WorseFit().match(Job.builder(1).cores(1).build(), hosts));
	}

	@Test
	public void testAccpetingBestHost()
	{
		Host host1 = Host.builder().cores(2).memory(16).build();
		Host host2 = Host.builder().cores(2).memory(15).build();
		Host host3 = Host.builder().cores(2).memory(8).build();
		List<Host> hosts = newArrayList(host3, host2, host1);
		assertEquals(host1, new WorseFit().match(Job.builder(1).cores(1).memory(4).build(), hosts));
	}

}
