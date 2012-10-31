package sim.scheduling;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.Clock;
import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.model.Host;
import sim.model.Job;

public class DispatcherTest
{

	@Test
	public void testDispatch()
	{
		int currentTime = 1;
		int jobLength = 1;
		Job job = Job.create(jobLength).priority(0).submitTime(0).cores(0).memory(0).build();
		Host host = mock(Host.class);
		EventQueue eventQueue = new EventQueue(new Clock());
		new Dispatcher(eventQueue).dipatch(job, host, currentTime);
		verify(host).dispatchJob(job);
		assertEquals(job.startTime(), currentTime);
		assertEquals(1, eventQueue.size());
		Event finish = eventQueue.peek();
		assertEquals(jobLength + currentTime, finish.time());
	}

}
