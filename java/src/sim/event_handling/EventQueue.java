package sim.event_handling;

import static com.google.common.collect.Lists.*;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import sim.Clock;
import sim.events.Event;
import sim.events.Finish;

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
	public List<Finish> clearRunningJobs()
	{
		List<Finish> $ = newArrayList();
		Iterator<Event> it = iterator();
		while (it.hasNext())
		{
			Event event = it.next();
			if (event instanceof Finish)
			{
				Finish finish = (Finish) event;
				$.add(finish);
				it.remove();
			}
		}
		return $;
	}

}
