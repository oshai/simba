package sim.event_handling;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.Clock;
import sim.events.Event;
import sim.events.NoOp;

public class EventQueueTest
{

	@Test
	public void testEventComparator()
	{
		assertEquals(1, new EventComparator().compare(new Event(1, null), new Event(0, null)));
		assertEquals(-1, new EventComparator().compare(new Event(0, null), new Event(1, null)));
		assertEquals(0, new EventComparator().compare(new Event(0, null), new Event(0, null)));
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

}
