package sim.event_handling;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;

import sim.Clock;
import sim.events.Event;
import sim.events.Finish;
import sim.events.NoOp;
import sim.events.Submit;
import sim.model.Job;

public class EventQueueTest
{

	@Test
	public void testEventComparator()
	{
		assertEquals(1, new EventComparator().compare(new Event(1, null), new Event(0, null)));
		assertEquals(-1, new EventComparator().compare(new Event(0, null), new Event(1, null)));
		Event o1 = new Event(0, Job.builder(1).id(1).build());
		Event o2 = new Event(0, Job.builder(1).id(2).build());
		assertEquals(-1, new EventComparator().compare(o1, o2));
		assertEquals(1, new EventComparator().compare(o2, o1));
	}

	@Test
	public void testEventComparatorNoOverflow()
	{
		assertEquals(1, new EventComparator().compare(new Event(Long.MAX_VALUE, null), new Event(1, null)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEventInPastNotAccepted()
	{
		Clock clock = mock(Clock.class);
		when(clock.time()).thenReturn(4L);
		EventQueue eventQueue = new EventQueue(clock);
		eventQueue.add(new NoOp(4));
	}

	@Test
	public void testEventNotInPastAccepted()
	{
		Clock clock = mock(Clock.class);
		when(clock.time()).thenReturn(4L);
		EventQueue eventQueue = new EventQueue(clock);
		NoOp event = new NoOp(5);
		eventQueue.add(event);
		assertEquals(event, eventQueue.iterator().next());
	}

	@Test
	public void testRemoveRunningJobs()
	{
		Clock clock = mock(Clock.class);
		when(clock.time()).thenReturn(-1L);
		EventQueue eventQueue = new EventQueue(clock);
		Event finish = new Finish(0, mock(Job.class), null);
		Event submit = new Submit(mock(Job.class));
		eventQueue.add(finish);
		eventQueue.add(submit);
		List<Finish> runningJobs = eventQueue.clearRunningJobs();
		assertEquals(finish, runningJobs.get(0));
		assertEquals(1, runningJobs.size());
		assertEquals(1, eventQueue.size());
	}

	@Test
	public void testToString() throws Exception
	{
		assertFalse(new EventQueue(mock(Clock.class)).toString().contains("@"));
	}
}
