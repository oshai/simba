package sim.distributed;

import sim.Clock;
import sim.JobFinisher;
import sim.Looper;
import sim.SimbaConfiguration;
import sim.collectors.IntervalCollector;
import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.model.Cluster;
import sim.model.Host;

public class DistributedLooper extends Looper
{

	private final Cluster cluster;

	public DistributedLooper(Clock clock, EventQueue eventQueue, IntervalCollector hostCollector, JobFinisher jobFinisher, SimbaConfiguration simbaConsts,
			Cluster cluster)
	{
		super(clock, eventQueue, hostCollector, jobFinisher, simbaConsts);
		this.cluster = cluster;
	}

	@Override
	protected boolean submitJob(Event event)
	{
		Host host = cluster.hosts().get(0);
		return host != null;
	}

	@Override
	protected int schedule(long time)
	{
		return 0;
	}

	@Override
	protected int sizeOfWaitingQueue()
	{
		return 0;
	}

}
