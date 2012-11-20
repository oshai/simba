package sim.scheduling;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.model.Job;

public class ByTraceSchedulerTest
{

	@Test
	public void testJobDispatched()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job = Job.create(1).startTime(1).build();
		waitingQueue.add(job);
		Dispatcher dispatcher = mock(Dispatcher.class);
		Scheduler scheduler = new ByTraceScheduler(waitingQueue, dispatcher);
		long time = 1;
		scheduler.schedule(time);
		assertTrue(waitingQueue.isEmpty());
		verify(dispatcher).dipatch(job, null, time);
	}

	@Test
	public void testJobNotDispatched()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job = Job.create(1).startTime(1).build();
		waitingQueue.add(job);
		Dispatcher dispatcher = mock(Dispatcher.class);
		Scheduler scheduler = new ByTraceScheduler(waitingQueue, dispatcher);
		long time = 0;
		scheduler.schedule(time);
		assertEquals(1, waitingQueue.size());
	}

}
