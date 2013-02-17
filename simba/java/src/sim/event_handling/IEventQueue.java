package sim.event_handling;

import java.util.Iterator;
import java.util.List;

import sim.events.Event;
import sim.events.Finish;

public interface IEventQueue
{

	boolean isEmpty();

	void add(Event event);

	Event peek();

	Event removeFirst();

	int size();

	Iterator<Event> iterator();

	List<Finish> clearRunningJobs();

}