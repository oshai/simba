package sim.event_handling;

import static com.google.common.collect.Lists.*;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import sim.Clock;
import sim.events.Event;
import sim.events.Finish;
import sim.events.Submit;

import com.google.common.collect.MinMaxPriorityQueue;

public class EventsList implements IEventQueue
{
	private final MinMaxPriorityQueue<Event> queue = MinMaxPriorityQueue.orderedBy(new EventComparator()).create();
	private final List<Event> finishedQueue = newArrayList();
	private final Clock clock;

	@Inject
	public EventsList(Clock clock)
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
		if (event instanceof Submit)
		{
			queue.add(event);
		}
		if (event instanceof Finish)
		{
			finishedQueue.add(event);
		}
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
		return queue.peek();
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
		Iterator<Event> $ = newArrayList(finishedQueue).iterator();
		finishedQueue.clear();
		return $;
	}

}
