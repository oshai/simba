package sim.distributed;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.waiting_queue.SetWaitingQueue;

public class DistributedJobDispatcherTest
{

	@Test
	public void testShoudBeDsipatched() throws Exception
	{
		Job job = Job.builder(100).build();
		SetWaitingQueue jobs = new SetWaitingQueue();
		jobs.add(job);
		EventQueue eq = mock(EventQueue.class);
		DistributedJobDispatcher tested = createTested(jobs, eq);
		tested.dispatch(job, mock(Host.class), 0);
		verify(eq).add(any(Event.class));
		assertEquals(0, jobs.size());
	}

	private DistributedJobDispatcher createTested(SetWaitingQueue jobs, EventQueue eq)
	{
		return new DistributedJobDispatcher(eq, jobs);
	}

	@Test
	public void testShoudNotBeDsipatchedIfNotInQueue() throws Exception
	{
		Job job = Job.builder(100).build();
		SetWaitingQueue jobs = new SetWaitingQueue();
		EventQueue eq = mock(EventQueue.class);
		DistributedJobDispatcher tested = createTested(jobs, eq);
		tested.dispatch(job, mock(Host.class), 0);
		verify(eq, never()).add(any(Event.class));
	}
}
