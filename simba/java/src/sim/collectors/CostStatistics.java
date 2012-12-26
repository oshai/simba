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
	private final WaitingQueueForStatistics waitingQueue;
	private final AllocationConfiguration configuration;

	public CostStatistics(Cluster cluster, WaitingQueueForStatistics queue, AllocationConfiguration configuration)
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

	private void updateWaitingJobs(Map<String, Qslot> qs)
	{
		for (Job job : waitingQueue)
		{
			Qslot qslot = qs.get(job.qslot());
			if (asserter().assertNotNull(qslot, "didnt find qslot " + job.qslot() + " for job " + job))
			{
				qslot.hasWaitingJobs(true);
				qslot.updateLongestWaitingJob(job);
			}
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
		for (Entry<String, QslotConfiguration> e : configuration.getAll().entrySet())
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

	private void updateGettingNow(Collection<Qslot> qs, double sumRunning)
	{
		if (!Double.valueOf(0.0).equals(sumRunning))
		{
			for (Qslot q : qs)
			{
				q.gettingNow(q.cost() / sumRunning);
			}
		}
	}

}
