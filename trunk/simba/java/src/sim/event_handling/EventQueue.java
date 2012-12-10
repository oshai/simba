package sim.event_handling;

import java.util.Iterator;

import javax.inject.Inject;

import sim.Clock;
import sim.events.Event;

import com.google.common.collect.MinMaxPriorityQueue;

public class EventQueue
{
	private final MinMaxPriorityQueue<Event> queue = MinMaxPriorityQueue.orderedBy(new EventComparator()).create();
	private final Clock clock;

	@Inject
	public EventQueue(Clock clock)
	{
		this.clock = clock;
	}

	public boolean isEmpty()
	{
		return queue.isEmpty();
	}

	public void add(Event event)
	{
		validate(event);
		queue.add(event);
	}

	private void validate(Event event)
	{
		if (event.time() <= clock.time())
		{
			throw new IllegalArgumentException("event is in the past " + event);
		}
	}

	public Event peek()
	{
		return queue.peekFirst();
	}

	public Event removeFirst()
	{
		return queue.removeFirst();
	}

	public int size()
	{
		return queue.size();
	}

	public Iterator<Event> iterator()
	{
		return queue.iterator();
	}

	@Override
	public String toString()
	{
		return "EventQueue [queue=" + queue + ", clock=" + clock.time() + "]";
	}

}
