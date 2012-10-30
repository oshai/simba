package sim.scheduling.best_fit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.JobFinisher;
import sim.collectors.JobCollector;
import sim.event_handling.EventQueue;
import sim.events.Finish;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.Dispatcher;
import sim.scheduling.WaitingQueue;
import sim.scheduling.matchers.FirstFit;
import sim.scheduling.matchers.Matcher;

public class BestFitSchedulerTest
{
	@Test
	public void testNoDispatch()
	{
		Dispatcher dispatcher = mock(Dispatcher.class);
		WaitingQueue waitingQueue = new WaitingQueue();
		Cluster cluster = new Cluster();
		Matcher matcher = new FirstFit();
		waitingQueue.add(Job.Builder.create(1).cores(1).memory(0).build());
		cluster.add(Host.Builder.create().build());
		SortedHosts hosts = new SortedHosts(cluster);
		new BestFitScheduler(waitingQueue, hosts, matcher, dispatcher).schedule(0);
		verifyZeroInteractions(dispatcher);
	}
	
	@Test
	public void testDispatch()
	{
		Dispatcher dispatcher = mock(Dispatcher.class);
		WaitingQueue waitingQueue = new WaitingQueue();
		Cluster cluster = new Cluster();
		Matcher matcher = new FirstFit();
		Job job = Job.Builder.create(1).cores(1).memory(0).build();
		waitingQueue.add(job);
		Host host = Host.Builder.create().cores(1).memory(1).build();
		cluster.add(host);
		SortedHosts hosts = new SortedHosts(cluster);
		new BestFitScheduler(waitingQueue, hosts, matcher, dispatcher).schedule(0);
		verify(dispatcher).dipatch(job, host, 0);
	}
	
	@Test
	public void testDispatchInHostWithLessMemory()
	{
		Dispatcher dispatcher = mock(Dispatcher.class);
		WaitingQueue waitingQueue = new WaitingQueue();
		Cluster cluster = new Cluster();
		Matcher matcher = new FirstFit();
		Job job = Job.Builder.create(1).cores(1).memory(0).build();
		waitingQueue.add(job);
		cluster.add(Host.Builder.create().cores(1).memory(1).build());
		Host host = Host.Builder.create().cores(1).memory(0).build();
		cluster.add(host);
		SortedHosts hosts = new SortedHosts(cluster);
		new BestFitScheduler(waitingQueue, hosts, matcher, dispatcher).schedule(0);
		verify(dispatcher).dipatch(job, host, 0);
	}
	
	@Test
	public void testDispatchInHostWithLessMemory2()
	{
		Dispatcher dispatcher = mock(Dispatcher.class);
		WaitingQueue waitingQueue = new WaitingQueue();
		Cluster cluster = new Cluster();
		Matcher matcher = new FirstFit();
		Job job = Job.Builder.create(1).cores(1).memory(0).build();
		waitingQueue.add(job);
		Host host = Host.Builder.create().cores(1).memory(0).build();
		cluster.add(host);
		cluster.add(Host.Builder.create().cores(1).memory(1).build());
		SortedHosts hosts = new SortedHosts(cluster);
		new BestFitScheduler(waitingQueue, hosts, matcher, dispatcher).schedule(0);
		verify(dispatcher).dipatch(job, host, 0);
	}
	
	@Test
	public void testAfterDispatchHostSortIsCorrect()
	{
		Dispatcher dispatcher = new Dispatcher(mock(EventQueue.class));
		WaitingQueue waitingQueue = new WaitingQueue();
		Cluster cluster = new Cluster();
		Matcher matcher = new FirstFit();
		Job job = Job.Builder.create(1).cores(0).memory(3).build();
		waitingQueue.add(job);
		Host host = Host.Builder.create().cores(1).memory(2).build();
		Host host2 = Host.Builder.create().cores(1).memory(4).build();
		cluster.add(host);
		cluster.add(host2);
		SortedHosts hosts = new SortedHosts(cluster);
		BestFitScheduler scheduler = new BestFitScheduler(waitingQueue, hosts, matcher, dispatcher);
		scheduler.schedule(0);
		Job job2 = Job.Builder.create(1).cores(0).memory(1).build();
		waitingQueue.add(job2);
		scheduler.schedule(1);
		assertTrue(waitingQueue.isEmpty());
		assertEquals(0, host2.availableMemory(), 0.01);
	}
	
	// this is integration tests - should not be here
	@Test
	public void testAfterJobFinishHostSortIsCorrect()
	{
		Dispatcher dispatcher = new Dispatcher(mock(EventQueue.class));
		WaitingQueue waitingQueue = new WaitingQueue();
		Cluster cluster = new Cluster();
		Matcher matcher = new FirstFit();
		Job job = Job.Builder.create(1).cores(0).memory(3).build();
		Job job2 = Job.Builder.create(1).priority(1).cores(0).memory(1).build();
		Job job3 = Job.Builder.create(1).cores(0).memory(2).build();
		Host host = Host.Builder.create().cores(1).memory(2).build();
		Host host2 = Host.Builder.create().cores(1).memory(4).build();
		cluster.add(host);
		cluster.add(host2);
		waitingQueue.add(job);
		waitingQueue.add(job2);
		SortedHosts hosts = new SortedHosts(cluster);
		BestFitScheduler scheduler = new BestFitScheduler(waitingQueue, hosts, matcher, dispatcher);
		scheduler.schedule(0);
		assertTrue(waitingQueue.isEmpty());
		assertEquals(0, host2.availableMemory(), 0.01);
		JobFinisher jobFinisher = new JobFinisher(mock(JobCollector.class), hosts);
		jobFinisher.finish(new Finish(0, job, host2));
		waitingQueue.add(job3);
		scheduler.schedule(1);
		assertTrue(waitingQueue.isEmpty());
		assertEquals(3, host2.availableMemory(), 0.01);
		assertEquals(0, host.availableMemory(), 0.01);
	}
	
}
