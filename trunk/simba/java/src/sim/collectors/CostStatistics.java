package sim.collectors;

import static utils.assertions.Asserter.*;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.WaitingQueueForStatistics;

import com.google.common.collect.Maps;

public class CostStatistics
{
	private final Cluster cluster;
	private final Map<String, QslotConfiguration> configuration;
	private final WaitingQueueForStatistics waitingQueue;

	public CostStatistics(Cluster cluster, Map<String, QslotConfiguration> configuration, WaitingQueueForStatistics queue)
	{
		this.cluster = cluster;
		this.configuration = configuration;
		this.waitingQueue = queue;
	}

	public Map<String, Qslot> apply()
	{
		Map<String, Qslot> $ = updateQslotsCost();
		updateWaitingJobs($);
		double sumAllocations = 0.0;
		double sumRunningAllocations = 0.0;
		double sumWaitingAllocations = 0.0;
		double sumWaitingRunningAllocations = 0.0;
		for (Entry<String, Qslot> e : $.entrySet())
		{
			if (e.getValue().hasRunningJobs())
			{
				sumRunningAllocations += e.getValue().configuration().allocation();
			}
			if (e.getValue().hasWaitingJobs())
			{
				sumWaitingAllocations += e.getValue().configuration().allocation();
			}
			if (e.getValue().hasWaitingJobs() || e.getValue().hasRunningJobs())
			{
				sumWaitingRunningAllocations += e.getValue().configuration().allocation();
			}
		}
		for (QslotConfiguration c : configuration.values())
		{
			sumAllocations += c.allocation();
		}
		for (Qslot q : $.values())
		{
			q.absoluteShouldGet(q.configuration().allocation() / sumAllocations);
			q.relativeRunningShouldGet(q.hasRunningJobs() ? q.configuration().allocation() / sumRunningAllocations : 0.0);
			q.relativeWaitingShouldGet(q.hasWaitingJobs() ? q.configuration().allocation() / sumWaitingAllocations : 0.0);
			q.relativeShouldGet(q.hasWaitingJobs() || q.hasRunningJobs() ? q.configuration().allocation() / sumWaitingRunningAllocations : 0.0);
		}
		return $;
	}

	private void updateWaitingJobs(Map<String, Qslot> $)
	{
		for (Job job : waitingQueue)
		{
			Qslot qslot = $.get(job.qslot());
			asserter().assertNotNull(qslot, "didnt find qslot " + job.qslot() + " for job " + job);
			qslot.hasWaitingJobs(true);
		}
	}

	private Map<String, Qslot> updateQslotsCost()
	{
		Map<String, Qslot> $ = Maps.newHashMap();
		double sumRunning = updateQslotRunningCost($);
		updateGettingNow($.values(), sumRunning);
		return $;
	}

	private double updateQslotRunningCost(Map<String, Qslot> $)
	{
		for (Entry<String, QslotConfiguration> e : configuration.entrySet())
		{
			Qslot qslot = new Qslot(e.getValue());
			$.put(e.getKey(), qslot);
		}
		double sumRunning = 0;
		for (Host h : cluster.hosts())
		{
			for (Job j : h.jobs())
			{
				Qslot qslot = $.get(j.qslot());
				asserter().assertNotNull(qslot, "didnt find qslot " + j.qslot());
				qslot.addCost(j.cost());
				sumRunning += j.cost();
				qslot.hasRunningJobs(true);
			}
		}
		return sumRunning;
	}

	private void updateGettingNow(Collection<Qslot> qslots, double sumRunning)
	{
		if (!Double.valueOf(0.0).equals(sumRunning))
		{
			for (Qslot q : qslots)
			{
				q.gettingNow(q.cost() / sumRunning);
			}
		}
	}

}
