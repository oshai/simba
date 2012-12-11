package sim.scheduling.reserving;

import static com.google.common.collect.Maps.*;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sim.SimbaConfiguration;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.JobDispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.graders.Grader;

public class MaxCostScheduler extends ReservingScheduler implements Scheduler
{
	private static final Logger log = Logger.getLogger(MaxCostScheduler.class);
	private final List<ReservingScheduler> schedulers;

	public MaxCostScheduler(AbstractWaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher, SimbaConfiguration simbaConfiguration,
			List<ReservingScheduler> schedulers)
	{
		super(waitingQueue, cluster, grader, dispatcher, simbaConfiguration);
		this.schedulers = schedulers;
		if (schedulers.isEmpty())
		{
			throw new IllegalArgumentException("must have at least one scheduler");
		}
	}

	@SuppressWarnings("null")
	@Override
	protected Map<Job, Host> selectJobsToDispatch(long time)
	{
		Map<Job, Host> $ = newHashMap();
		double maxCost = -1;
		ReservingScheduler maxScheduler = null;
		for (ReservingScheduler scheduler : schedulers)
		{
			Map<Job, Host> current = scheduler.scheduleWithoutDispatch(time);
			double currentCost = calcCost(current);
			if (currentCost > maxCost)
			{
				$ = current;
				maxCost = currentCost;
				maxScheduler = scheduler;
			}
		}
		if (shouldReport(time))
		{
			log.info("selectJobsToDispatch() - cost is " + maxCost + "for scheduler " + maxScheduler.getClass().getSimpleName());
		}
		return $;
	}

	private double calcCost(Map<Job, Host> current)
	{
		double $ = 0;
		for (Job job : current.keySet())
		{
			$ += job.cost();
		}
		return $;
	}
}
