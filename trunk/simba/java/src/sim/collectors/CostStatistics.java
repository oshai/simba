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
	public static class SumAllocations
	{
		public double sumAllocations;
		public double sumRunningAllocations;
		public double sumWaitingAllocations;
		public double sumWaitingRunningAllocations;

		public void update(Qslot value)
		{
			sumAllocations += value.allocation();
			if (value.hasWaitingJobs() || value.hasRunningJobs())
			{
				sumWaitingRunningAllocations += value.allocation();
			}
			if (value.hasRunningJobs())
			{
				sumRunningAllocations += value.allocation();
			}
			if (value.hasWaitingJobs())
			{
				sumWaitingAllocations += value.allocation();
			}
		}
	}

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
		updateQslotShouldGet($.values());
		return $;
	}

	private void updateQslotShouldGet(Collection<Qslot> qs)
	{
		updateQslotShouldGet(createSumAllocations(qs), qs);
	}

	private SumAllocations createSumAllocations(Collection<Qslot> values)
	{
		SumAllocations sum = new SumAllocations();
		for (Qslot value : values)
		{
			sum.update(value);
		}
		return sum;
	}

	private void updateQslotShouldGet(SumAllocations sum, Collection<Qslot> values)
	{
		for (Qslot q : values)
		{
			q.absoluteShouldGet(q.allocation() / sum.sumAllocations);
			q.relativeRunningShouldGet(q.hasRunningJobs() ? q.allocation() / sum.sumRunningAllocations : 0.0);
			q.relativeWaitingShouldGet(q.hasWaitingJobs() ? q.allocation() / sum.sumWaitingAllocations : 0.0);
			q.relativeShouldGet(q.hasWaitingJobs() || q.hasRunningJobs() ? q.allocation() / sum.sumWaitingRunningAllocations : 0.0);
		}
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
