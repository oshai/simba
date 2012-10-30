package sim;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import sim.collectors.HostCollector;
import sim.collectors.JobCollector;
import sim.event_handling.EventQueue;
import sim.events.Submit;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.Dispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.WaitingQueue;
import sim.scheduling.first_fit.FirstFitScheduler;
import sim.scheduling.matchers.FirstFit;
import sim.scheduling.matchers.Matcher;

public class SystemTest
{
	private EventQueue eventQueue;
	private Clock clock;
	
	@Before
	public void beforeTest()
	{
		clock = new Clock();
		eventQueue = new EventQueue(clock);
	}
	
	@Test
	public void testOneJobOneHost()
	{
		Host host = Host.Builder.create().cores(1).memory(8).build();
		Job job = Job.Builder.create(3).priority(0).submitTime(2).cores(1).memory(4).build();
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
		Host host = Host.Builder.create().cores(2).memory(8).build();
		Job job1 = Job.Builder.create(3).priority(0).submitTime(2).cores(1).memory(4).build();
		Job job2 = Job.Builder.create(4).priority(0).submitTime(2).cores(1).memory(4).build();
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
		Host host = Host.Builder.create().cores(1).memory(8).build();
		Job job1 = Job.Builder.create(3).priority(0).submitTime(2).cores(1).memory(4).build();
		Job job2 = Job.Builder.create(4).priority(0).submitTime(3).cores(1).memory(4).build();
		ArrayList<Job> jobs = newArrayList(job1, job2);
		Looper looper = init(host, jobs);
		looper.execute();
		assertTrue(eventQueue.isEmpty());
	}
	
	private Looper init(Host host, ArrayList<Job> arrayList)
	{
		Cluster cluster = new Cluster();
		cluster.add(host);
		for (Job job : arrayList)
		{
			eventQueue.add(new Submit(job));
		}
		WaitingQueue waitingQueue = new WaitingQueue();
		Dispatcher dispatcher = new Dispatcher(eventQueue);
		Matcher matcher = new FirstFit();
		Scheduler scheduler = new FirstFitScheduler(waitingQueue, cluster, matcher, dispatcher);
		JobCollector jobCollector = new JobCollector();
		HostCollector statistics = new HostCollector(cluster, 1);
		JobFinisher jobFinisher = new JobFinisher(jobCollector, cluster);
		Looper looper = new Looper(clock, eventQueue, waitingQueue, scheduler, statistics, jobFinisher);
		return looper;
	}
}
