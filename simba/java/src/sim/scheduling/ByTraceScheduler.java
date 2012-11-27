package sim.scheduling;

import java.util.Iterator;

import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;

public class ByTraceScheduler implements Scheduler
{
	private final AbstractWaitingQueue waitingQueue;
	private final JobDispatcher dispatcher;
	private Host host;

	public ByTraceScheduler(AbstractWaitingQueue waitingQueue, Cluster cluster, JobDispatcher dispatcher)
	{
		this.waitingQueue = waitingQueue;
		this.dispatcher = dispatcher;
		updateCluster(cluster);
	}

	private void updateCluster(Cluster cluster)
	{
		double memory = 0;
		double cores = 0;
		for (Host host1 : cluster.hosts())
		{
			memory += host1.memory();
			cores += host1.cores();
		}
		cluster.hosts().clear();
		host = Host.create().cores(cores).memory(memory).build();
		cluster.add(host);
	}

	@Override
	public int schedule(long time)
	{
		int $ = 0;
		Iterator<Job> iterator = waitingQueue.iterator();
		while (iterator.hasNext())
		{
			Job job = iterator.next();
			if (job.startTime() <= time)
			{
				iterator.remove();
				dispatcher.dispatch(job, host, time);
				$++;
			}
		}
		return $;
	}
}
