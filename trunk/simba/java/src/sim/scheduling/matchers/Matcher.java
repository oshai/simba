package sim.scheduling.matchers;

import sim.model.Host;
import sim.model.Job;

public interface Matcher
{
	public Host match(Job job, Iterable<Host> list);
}
