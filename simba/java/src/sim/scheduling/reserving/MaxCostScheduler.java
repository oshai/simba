package sim.scheduling.reserving;

import static com.google.common.collect.Maps.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
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
	private final ScheduleCalculator scheduleCalculator;

	public MaxCostScheduler(AbstractWaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher, SimbaConfiguration simbaConfiguration,
			List<ReservingScheduler> schedulers, ScheduleCalculator scheduleCalculator)
	{
		super(waitingQueue, cluster, grader, dispatcher, simbaConfiguration);
		this.schedulers = schedulers;
		this.scheduleCalculator = scheduleCalculator;
		if (schedulers.isEmpty())
		{
			throw new IllegalArgumentException("must have at least one scheduler");
		}
	}

	@Override
	protected Map<Job, Host> selectJobsToDispatch(long time)
	{
		Map<Job, Host> $ = newHashMap();
		double maxCost = -1;
		String maxScheduler = null;
		Iterable<Pair<String, Map<Job, Host>>> scheduleResults = scheduleCalculator.calculateSchedule(schedulers, time);
		for (Pair<String, Map<Job, Host>> current : scheduleResults)
		{
			double currentCost = calcCost(current.getValue());
			if (currentCost > maxCost)
			{
				$ = current.getValue();
				maxCost = currentCost;
				maxScheduler = current.getKey();
			}
		}
		if (shouldReport(time))
		{
			log.info("cost is " + maxCost + "for scheduler " + maxScheduler);
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
