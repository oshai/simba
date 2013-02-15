package sim.event_handling;

import java.util.Iterator;

import sim.events.Event;

public interface IEventQueue
{

	boolean isEmpty();

	void add(Event event);

	Event peek();

	Event removeFirst();

	int size();

	Iterator<Event> iterator();

	Iterator<Event> clearRunningJobs();

}