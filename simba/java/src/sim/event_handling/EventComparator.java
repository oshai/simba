package sim.event_handling;

import java.util.Comparator;

import sim.events.Event;

public class EventComparator implements Comparator<Event>
{
	@Override
	public int compare(Event o1, Event o2)
	{
		if (o1.time() == o2.time())
		{
			return o1.hashCode() - o2.hashCode();
		}
		return o1.time() - o2.time() > 0 ? 1 : -1;
	}

}