package sim;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import sim.event_handling.IEventQueue;
import sim.events.Submit;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class SystemTest
{
	private IEventQueue eventQueue;
	private Injector injector;

	@Before
	public void beforeTest()
	{
		injector = Guice.createInjector(new SemiProductionSimbaConfiguration());
		eventQueue = injector.getInstance(IEventQueue.class);
	}

	@Test
	public void testOneJobOneHost()
	{
		Host host = Host.builder().cores(1).memory(8).build();
		Job job = Job.builder(3).priority(0).submitTime(2).cores(1).memory(4).build();
		ArrayList<Job> jobs = newArrayList(job);
		Looper looper = init(host, jobs);
		looper.tick();
		assertEquals(newArrayList(), host.jobs());
		looper.tick();
		assertEquals(jobs, host.jobs());
		looper.tick();
		assertEquals(jobs, host.jobs());
		looper.tick();
		assertEquals(jobs, host.jobs());
		looper.tick();
		assertEquals(newArrayList(), host.jobs());
		assertTrue(eventQueue.isEmpty());
	}

	@Test
	public void test2Jobs1Host()
	{
		Host host = Host.builder().cores(2).memory(8).build();
		Job job1 = Job.builder(3).id(1).priority(0).submitTime(2).cores(1).memory(4).build();
		Job job2 = Job.builder(4).id(2).priority(0).submitTime(2).cores(1).memory(4).build();
		ArrayList<Job> jobs = newArrayList(job1, job2);
		Looper looper = init(host, jobs);
		looper.tick();
		assertEquals(newArrayList(), host.jobs());
		looper.tick();
		assertEquals(jobs, host.jobs());
		looper.tick();
		assertEquals(jobs, host.jobs());
		looper.tick();
		assertEquals(jobs, host.jobs());
		looper.tick();
		assertEquals(newArrayList(job2), host.jobs());
		looper.tick();
		assertEquals(newArrayList(), host.jobs());
		assertTrue(eventQueue.isEmpty());
	}

	@Test
	public void test2Jobs1HostWithWaiting()
	{
		Host host = Host.builder().cores(1).memory(8).build();
		Job job1 = Job.builder(3).priority(0).submitTime(2).cores(1).memory(4).build();
		Job job2 = Job.builder(4).priority(0).submitTime(3).cores(1).memory(4).build();
		ArrayList<Job> jobs = newArrayList(job1, job2);
		Looper looper = init(host, jobs);
		looper.execute();
		assertTrue(eventQueue.isEmpty());
	}

	private Looper init(Host host, ArrayList<Job> jobs)
	{
		Cluster cluster = injector.getInstance(Cluster.class);
		cluster.add(host);
		for (Job job : jobs)
		{
			eventQueue.add(new Submit(job));
		}
		Looper looper = injector.getInstance(Looper.class);
		return looper;
	}
}
