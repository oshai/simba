package sim.distributed;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.Clock;
import sim.JobFinisher;
import sim.SimbaConfiguration;
import sim.collectors.IntervalCollector;
import sim.event_handling.EventQueue;
import sim.events.Submit;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;

public class DistributedLooperTest
{

	@Test
	public void testEmpty() throws Exception
	{
		DistributedLooper l = createLooper();
		Job j = Job.create(1).build();
		assertTrue(l.submitJob(new Submit(j)));
	}

	private DistributedLooper createLooper()
	{

		Clock clock = null;
		EventQueue eventQueue = null;
		IntervalCollector collector = mock(IntervalCollector.class);
		JobFinisher jobFinisher = null;
		SimbaConfiguration consts = null;
		Cluster cluster = new Cluster();
		cluster.add(Host.create().build());
		return new DistributedLooper(clock, eventQueue, collector, jobFinisher, consts, cluster);
	}
}
