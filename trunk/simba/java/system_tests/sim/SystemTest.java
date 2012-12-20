package sim;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import sim.collectors.IntervalCollector;
import sim.collectors.JobCollector;
import sim.collectors.MiscStatisticsCollector;
import sim.collectors.WaitingQueueStatistics;
import sim.configuration.ProductionSimbaConfiguration.LooperFactory;
import sim.event_handling.EventQueue;
import sim.events.Submit;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.JobDispatcher;
import sim.scheduling.LinkedListWaitingQueue;
import sim.scheduling.Scheduler;
import sim.scheduling.SimpleScheduler;
import sim.scheduling.matchers.FirstFit;
import sim.scheduling.matchers.Matcher;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class SystemTest
{
	private EventQueue eventQueue;
	private Clock clock;
	private Injector injector;

	@Before
	public void beforeTest()
	{
		clock = new Clock();
		eventQueue = new EventQueue(clock);
		injector = Guice.createInjector(new SemiProductionSimbaConfiguration());
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
		Job job1 = Job.builder(3).priority(0).submitTime(2).cores(1).memory(4).build();
		Job job2 = Job.builder(4).priority(0).submitTime(2).cores(1).memory(4).build();
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

	private Looper init(Host host, ArrayList<Job> arrayList)
	{
		Cluster cluster = new Cluster();
		cluster.add(host);
		for (Job job : arrayList)
		{
			eventQueue.add(new Submit(job));
		}
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		WaitingQueueStatistics waitingQueueStatistics = new WaitingQueueStatistics(waitingQueue, Integer.MAX_VALUE, clock);
		JobDispatcher dispatcher = new JobDispatcher(eventQueue);
		Matcher matcher = new FirstFit();
		Scheduler scheduler = new SimpleScheduler(waitingQueue, cluster, matcher, dispatcher);
		JobCollector jobCollector = new JobCollector();
		jobCollector.init();
		JobFinisher jobFinisher = new JobFinisher(jobCollector);
		IntervalCollector statistics = new MiscStatisticsCollector(cluster, 1, waitingQueueStatistics, jobFinisher);
		statistics.init();
		Looper looper = injector.getInstance(LooperFactory.class).create(clock, eventQueue, waitingQueue, scheduler, statistics, jobFinisher);
		return looper;
	}
}
