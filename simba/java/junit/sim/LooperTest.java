package sim;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.collectors.MiscStatisticsCollector;
import sim.configuration.ProductionSimbaConfiguration;
import sim.event_handling.EventQueue;
import sim.events.Finish;
import sim.events.NoOp;
import sim.events.Submit;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.ByTraceScheduler;
import sim.scheduling.JobDispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.SimpleScheduler;
import sim.scheduling.waiting_queue.AbstractWaitingQueue;
import sim.scheduling.waiting_queue.LinkedListWaitingQueue;

public class LooperTest
{

	private static final int BUCKET_SIZE = 7;

	@Test
	public void testEmpty()
	{
		Clock clock = new Clock();
		Looper looper = createLooper(clock, new EventQueue(clock), new LinkedListWaitingQueue(), mock(SimpleScheduler.class));
		looper.execute();
	}

	@Test
	public void testClockTick()
	{
		Clock clock = new Clock();
		EventQueue eventQueue = new EventQueue(clock);
		eventQueue.add(new NoOp(1));
		SimpleScheduler scheduler = mock(SimpleScheduler.class);
		Looper looper = createLooper(clock, eventQueue, new LinkedListWaitingQueue(), scheduler);
		looper.execute();
		verify(scheduler).schedule(1);
		assertEquals(2, clock.time());
		assertTrue(eventQueue.isEmpty());
	}

	@Test
	public void testSubmit0IsNotExit()
	{
		Clock clock = new Clock();
		EventQueue eventQueue = new EventQueue(clock);
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		waitingQueue.add(Job.builder(5).build());
		Scheduler scheduler = new ByTraceScheduler(waitingQueue, new Cluster(), new JobDispatcher(eventQueue));
		Looper looper = createLooper(clock, eventQueue, waitingQueue, scheduler);
		looper.execute();
		assertEquals(7, clock.time());
		assertTrue(eventQueue.isEmpty());
		assertTrue(waitingQueue.isEmpty());
	}

	@Test
	public void testSubmitEvent()
	{
		Clock clock = new Clock();
		EventQueue eventQueue = new EventQueue(clock);
		Job job = Job.builder(1).priority(0).submitTime(1).cores(0).memory(0).build();
		eventQueue.add(new Submit(job));
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Looper looper = createLooper(clock, eventQueue, waitingQueue, mock(SimpleScheduler.class));
		looper.tick();
		assertEquals(1, clock.time());
		assertTrue(eventQueue.isEmpty());
		assertEquals(1, waitingQueue.size());
		assertEquals(job, waitingQueue.peek());
	}

	@Test
	public void testFinishEvent()
	{
		Clock clock = new Clock();
		EventQueue eventQueue = new EventQueue(clock);
		Job job = Job.builder(1).priority(0).submitTime(1).cores(0).memory(0).build();
		Host host = Host.builder().cores(0).memory(0).build();
		host.dispatchJob(job);
		eventQueue.add(new Finish(1, job, host));
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		JobFinisher jobFinisher = mock(JobFinisher.class);
		Looper looper = createLooper(clock, eventQueue, waitingQueue, mock(SimpleScheduler.class), jobFinisher, new ProductionSimbaConfiguration());
		looper.tick();
		assertEquals(1, clock.time());
		assertTrue(eventQueue.isEmpty());
		verify(jobFinisher).finish((Finish) anyObject());
	}

	@Test
	public void testNoEventsCycle()
	{
		Clock clock = new Clock();
		EventQueue eventQueue = new EventQueue(clock);
		Job job = Job.builder(1).priority(0).submitTime(12).cores(0).memory(0).build();
		eventQueue.add(new Submit(job));
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Scheduler scheduler = mock(SimpleScheduler.class);
		Looper looper = createLooper(clock, eventQueue, waitingQueue, scheduler);
		assertTrue(looper.tick());
		verify(scheduler).schedule(1);
		assertTrue(looper.tick());
		verifyNoMoreInteractions(scheduler);
	}

	@Test
	public void testNoEventsFirstCycle()
	{
		Clock clock = new Clock();
		EventQueue eventQueue = new EventQueue(clock);
		Job job = Job.builder(1).priority(0).submitTime(2).cores(0).memory(0).build();
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		waitingQueue.add(job);
		Scheduler scheduler = mock(SimpleScheduler.class);
		Looper looper = createLooper(clock, eventQueue, waitingQueue, scheduler);
		assertTrue(looper.tick());
		verify(scheduler).schedule(1);
	}

	@Test
	public void testYeshEventsCycle()
	{
		Clock clock = new Clock();
		EventQueue eventQueue = new EventQueue(clock);
		Job job = Job.builder(1).priority(0).submitTime(1).cores(0).memory(0).build();
		eventQueue.add(new Submit(job));
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		SimpleScheduler scheduler = mock(SimpleScheduler.class);
		Looper looper = createLooper(clock, eventQueue, waitingQueue, scheduler);
		assertTrue(looper.tick());
		verify(scheduler).schedule(1);
	}

	private Looper createLooper(Clock clock, EventQueue eventQueue, AbstractWaitingQueue waitingQueue, Scheduler scheduler)
	{
		return createLooper(clock, eventQueue, waitingQueue, scheduler, mock(JobFinisher.class), createConsts());
	}

	private Looper createLooper(Clock clock, EventQueue eventQueue, AbstractWaitingQueue waitingQueue, Scheduler scheduler, JobFinisher jobFinisher, SimbaConfiguration consts)
	{
		return new Looper(clock, eventQueue, waitingQueue, scheduler, mock(MiscStatisticsCollector.class), jobFinisher, consts);
	}

	@Test
	public void testBucketSimulationRunningJobsRemoved()
	{
		Clock clock = new Clock(BUCKET_SIZE - 1);
		EventQueue eventQueue = new EventQueue(clock);
		Job job = Job.builder(1).priority(0).submitTime(0).cores(0).memory(0).build();
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		eventQueue.add(new Finish(8, job, null));
		SimpleScheduler scheduler = mock(SimpleScheduler.class);
		SimbaConfiguration consts = createBucketConsts();
		Looper looper = createLooper(clock, eventQueue, waitingQueue, scheduler, mock(JobFinisher.class), consts);
		looper.tick();
		assertTrue(eventQueue.isEmpty());
	}

	@Test
	public void testBucketSimulationRemoveWaitingJobs()
	{
		Clock clock = new Clock(BUCKET_SIZE - 1);
		EventQueue eventQueue = new EventQueue(clock);
		Job job = Job.builder(1).priority(0).submitTime(0).cores(0).memory(0).build();
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		waitingQueue.add(job);
		SimpleScheduler scheduler = mock(SimpleScheduler.class);
		SimbaConfiguration consts = createBucketConsts();
		Looper looper = createLooper(clock, eventQueue, waitingQueue, scheduler, mock(JobFinisher.class), consts);
		looper.tick();
		assertTrue(eventQueue.isEmpty());
		assertTrue(waitingQueue.isEmpty());
	}

	@Test
	public void testBucketSimulationScheduleNotCalledNotOnBucketTime()
	{
		Clock clock = new Clock(BUCKET_SIZE + 1);
		EventQueue eventQueue = new EventQueue(clock);
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		SimpleScheduler scheduler = mock(SimpleScheduler.class);
		SimbaConfiguration consts = createBucketConsts();
		Looper looper = createLooper(clock, eventQueue, waitingQueue, scheduler, mock(JobFinisher.class), consts);
		assertTrue(looper.tick());
		verifyZeroInteractions(scheduler);
	}

	private SimbaConfiguration createConsts()
	{
		SimbaConfiguration consts = new ForTestingSimbaConfiguration();
		return consts;
	}

	private SimbaConfiguration createBucketConsts()
	{
		ForTestingSimbaConfiguration consts = new ForTestingSimbaConfiguration()
		{
			@Override
			public boolean isBucketSimulation()
			{
				return true;
			}

			@Override
			public long bucketSize()
			{
				return BUCKET_SIZE;
			}

		};
		return consts;
	}

}
