package sim.event_handling;

import java.util.Iterator;

import javax.inject.Inject;

import sim.Clock;
import sim.events.Event;

import com.google.common.collect.MinMaxPriorityQueue;

public class EventQueue implements IEventQueue
{
	private final MinMaxPriorityQueue<Event> queue = MinMaxPriorityQueue.orderedBy(new EventComparator()).create();
	private final Clock clock;

	@Inject
	public EventQueue(Clock clock)
	{
		this.clock = clock;
	}

	@Override
	public boolean isEmpty()
	{
		return queue.isEmpty();
	}

	@Override
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

	@Override
	public Event peek()
	{
		return queue.peekFirst();
	}

	@Override
	public Event removeFirst()
	{
		return queue.removeFirst();
	}

	@Override
	public int size()
	{
		return queue.size();
	}

	@Override
	public Iterator<Event> iterator()
	{
		return queue.iterator();
	}

	@Override
	public String toString()
	{
		return "EventQueue [queue=" + queue + ", clock=" + clock.time() + "]";
	}

	@Override
	public Iterator<Event> clearRunningJobs()
	{
		throw new UnsupportedOperationException();
	}

}
