package sim.distributed;

import sim.model.Job;

public interface HostSelector
{

	HostScheduler select(Job job);

}