package sim.scheduling.matchers;

import java.util.List;

import sim.model.Host;
import sim.model.Job;

public interface Matcher
{
	public Host match(Job job, List<Host> hosts);
}
