package sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;

import sim.collectors.HostCollector;
import sim.event_handling.EventQueue;
import sim.events.Finish;
import sim.events.NoOp;
import sim.events.Submit;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.Scheduler;
import sim.scheduling.SimpleScheduler;
import sim.scheduling.WaitingQueue;

public class LooperTest
{

	@Test
	public void testEmpty()
	{
		Clock clock = new Clock();
		Looper looper = new Looper(clock, new EventQueue(clock), new WaitingQueue(), mock(SimpleScheduler.class), mock(HostCollector.class),
				mock(JobFinisher.class));
		looper.execute();
	}

	@Test
	public void testClockTick()
	{
		Clock clock = new Clock();
		EventQueue eventQueue = new EventQueue(clock);
		eventQueue.add(new NoOp(1));
		SimpleScheduler scheduler = mock(SimpleScheduler.class);
		Looper looper = new Looper(clock, eventQueue, new WaitingQueue(), scheduler, mock(HostCollector.class), mock(JobFinisher.class));
		looper.setTimeToLog(1);
		looper.execute();
		verify(scheduler).schedule(1);
		assertEquals(1, clock.time());
		assertTrue(eventQueue.isEmpty());
	}

	@Test
	public void testSubmitEvent()
	{
		Clock clock = new Clock();
		EventQueue eventQueue = new EventQueue(clock);
		Job job = Job.Builder.create(1).priority(0).submitTime(1).cores(0).memory(0).build();
		eventQueue.add(new Submit(job));
		WaitingQueue waitingQueue = new WaitingQueue();
		Looper looper = new Looper(clock, eventQueue, waitingQueue, mock(SimpleScheduler.class), mock(HostCollector.class), mock(JobFinisher.class));
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
		Job job = Job.Builder.create(1).priority(0).submitTime(1).cores(0).memory(0).build();
		Host host = Host.Builder.create().cores(0).memory(0).build();
		host.dispatchJob(job);
		eventQueue.add(new Finish(1, job, host));
		WaitingQueue waitingQueue = new WaitingQueue();
		JobFinisher jobFinisher = mock(JobFinisher.class);
		Looper looper = new Looper(clock, eventQueue, waitingQueue, mock(SimpleScheduler.class), mock(HostCollector.class), jobFinisher);
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
		Job job = Job.Builder.create(1).priority(0).submitTime(2).cores(0).memory(0).build();
		eventQueue.add(new Submit(job));
		WaitingQueue waitingQueue = new WaitingQueue();
		Scheduler scheduler = mock(SimpleScheduler.class);
		Looper looper = new Looper(clock, eventQueue, waitingQueue, scheduler, mock(HostCollector.class), mock(JobFinisher.class));
		assertFalse(looper.tick());
		verifyZeroInteractions(scheduler);
	}

	@Test
	public void testYeshEventsCycle()
	{
		Clock clock = new Clock();
		EventQueue eventQueue = new EventQueue(clock);
		Job job = Job.Builder.create(1).priority(0).submitTime(1).cores(0).memory(0).build();
		eventQueue.add(new Submit(job));
		WaitingQueue waitingQueue = new WaitingQueue();
		SimpleScheduler scheduler = mock(SimpleScheduler.class);
		Looper looper = new Looper(clock, eventQueue, waitingQueue, scheduler, mock(HostCollector.class), mock(JobFinisher.class));
		assertTrue(looper.tick());
		verify(scheduler).schedule(1);
	}

}
