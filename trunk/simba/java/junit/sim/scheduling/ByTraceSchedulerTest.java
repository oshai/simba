package sim.scheduling;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.event_handling.EventQueue;
import sim.model.Job;

public class ByTraceSchedulerTest
{

	@Test
	public void testJobDispatched()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job = Job.create(1).startTime(1).build();
		waitingQueue.add(job);
		JobDispatcher dispatcher = new JobDispatcher(mock(EventQueue.class));
		Scheduler tested = new ByTraceScheduler(waitingQueue, dispatcher);
		long time = 1;
		tested.schedule(time);
		assertTrue(waitingQueue.isEmpty());
		verify(dispatcher).dispatch(job, null, time);
	}

	@Test
	public void testJobNotDispatched()
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Job job = Job.create(1).startTime(1).build();
		waitingQueue.add(job);
		JobDispatcher dispatcher = mock(JobDispatcher.class);
		Scheduler tested = new ByTraceScheduler(waitingQueue, dispatcher);
		long time = 0;
		tested.schedule(time);
		assertEquals(1, waitingQueue.size());
	}

}
