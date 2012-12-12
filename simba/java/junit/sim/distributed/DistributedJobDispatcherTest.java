package sim.distributed;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.Test;

import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.model.Host;
import sim.model.Job;

import com.google.common.collect.Sets;

public class DistributedJobDispatcherTest
{

	@Test
	public void testShoudBeDsipatched() throws Exception
	{
		Job job = Job.builder(100).build();
		Set<Job> jobs = Sets.newHashSet(job);
		EventQueue eq = mock(EventQueue.class);
		DistributedJobDispatcher tested = new DistributedJobDispatcher(eq, jobs);
		tested.dispatch(job, mock(Host.class), 0);
		verify(eq).add(any(Event.class));
		assertTrue(jobs.isEmpty());
	}

	@Test
	public void testShoudNotBeDsipatchedIfNotInQueue() throws Exception
	{
		Job job = Job.builder(100).build();
		Set<Job> jobs = Sets.newHashSet();
		EventQueue eq = mock(EventQueue.class);
		DistributedJobDispatcher tested = new DistributedJobDispatcher(eq, jobs);
		tested.dispatch(job, mock(Host.class), 0);
		verify(eq, never()).add(any(Event.class));
	}
}
