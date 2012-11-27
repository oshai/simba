package sim.event_handling;

import java.util.Iterator;

import javax.inject.Provider;

import sim.Clock;
import sim.events.Event;

import com.google.common.collect.MinMaxPriorityQueue;

public class EventQueue
{
	private final MinMaxPriorityQueue<Event> queue = MinMaxPriorityQueue.orderedBy(new EventComparator()).create();
	private final Provider<Clock> clockProvider;

	public EventQueue(Clock clock)
	{
		this(new ClockProvider(clock));
	}

	public EventQueue(Provider<Clock> clockProvider)
	{
		this.clockProvider = clockProvider;
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
		if (null != clockProvider.get() && event.time() <= clockProvider.get().time())
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
}
