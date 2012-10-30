package sim.scheduling.mix_fit;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;

public class HostPickerTest
{
	
	@Test
	public void testNoAccpetingHostNotFit()
	{
		List<Host> hosts = newArrayList(Host.Builder.create().build());
		assertNull(new HostPicker(hosts).getBestHost(Job.Builder.create(1).cores(1).build()));
	}
	
	@Test
	public void testAccpetingHostFit()
	{
		Host host = Host.Builder.create().cores(1).build();
		List<Host> hosts = newArrayList(host);
		assertEquals(host, new HostPicker(hosts).getBestHost(Job.Builder.create(1).cores(1).build()));
	}
	
	@Test
	public void testAccpetingBestHost()
	{
		Host host1 = Host.Builder.create().cores(2).memory(16).build();
		Host host2 = Host.Builder.create().cores(2).memory(15).build();
		Host host3 = Host.Builder.create().cores(2).memory(8).build();
		List<Host> hosts = newArrayList(host1, host2, host3);
		assertEquals(host3, new HostPicker(hosts).getBestHost(Job.Builder.create(1).cores(1).memory(4).build()));
	}
	
}
