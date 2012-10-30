package sim.scheduling.matchers;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;

public class MixFitTest
{

	@Test
	public void testNoAccpetingHostNotFit()
	{
		List<Host> hosts = newArrayList(Host.create().build());
		assertNull(new MixFit().match(Job.create((long) 1).cores(1).build(), hosts));
	}

	@Test
	public void testAccpetingHostFit()
	{
		Host host = Host.create().cores(1).build();
		List<Host> hosts = newArrayList(host);
		assertEquals(host, new MixFit().match(Job.create((long) 1).cores(1).build(), hosts));
	}

	@Test
	public void testAccpetingBestHost()
	{
		Host host1 = Host.create().cores(2).memory(16).build();
		Host host2 = Host.create().cores(2).memory(15).build();
		Host host3 = Host.create().cores(2).memory(8).build();
		List<Host> hosts = newArrayList(host1, host2, host3);
		assertEquals(host3, new MixFit().match(Job.create((long) 1).cores(1).memory(4).build(), hosts));
	}

	@Test
	public void testWhenEqualsUsingLessAvailableMemoryHost()
	{
		Host host1 = Host.create().cores(2).memory(16).build();
		Host host2 = Host.create().cores(1).memory(8).build();
		List<Host> hosts = newArrayList(host1, host2);
		assertEquals(host2, new MixFit().match(Job.create((long) 1).cores(1).memory(4).build(), hosts));
	}

}
