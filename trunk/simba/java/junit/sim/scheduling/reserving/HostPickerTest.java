package sim.scheduling.reserving;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.graders.Grader;

public class HostPickerTest
{
	@Test
	public void testHostCannotRunjob() throws Exception
	{
		List<Host> hosts = newArrayList(Host.builder().cores(1).memory(3).build());
		Job job = Job.builder(100).cores(2).build();
		HostPicker tested = createHostPicker(hosts);
		assertEquals(ReservingScheduler.DUMMY_HOST, tested.getBestHost(job));
		assertEquals(3, tested.maxAvailableMemory(), 0.1);
	}

	@Test
	public void testHostCanRunjob() throws Exception
	{
		Host host = Host.builder().cores(1).build();
		List<Host> hosts = newArrayList(host);
		Job job = Job.builder(100).cores(1).build();
		HostPicker tested = createHostPicker(hosts);
		assertEquals(host, tested.getBestHost(job));
	}

	private HostPicker createHostPicker(List<Host> hosts)
	{
		ReservingSchedulerUtils r = new ReservingSchedulerUtils(new ReservationsHolder());
		Grader g = mock(Grader.class);
		HostPicker tested = new HostPicker(r, hosts, g);
		return tested;
	}
}
