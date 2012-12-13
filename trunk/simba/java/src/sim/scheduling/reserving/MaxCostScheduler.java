package sim.scheduling.reserving;

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
	private final ScheduleCalculator scheduleCalculator;
	private final IMaxCostCollector maxCostCollector;

	public MaxCostScheduler(AbstractWaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher, SimbaConfiguration simbaConfiguration, List<ReservingScheduler> schedulers, ScheduleCalculator scheduleCalculator, IMaxCostCollector maxCostCollector)
	{
		super(waitingQueue, cluster, grader, dispatcher, simbaConfiguration);
		this.schedulers = schedulers;
		this.scheduleCalculator = scheduleCalculator;
		this.maxCostCollector = maxCostCollector;
		if (schedulers.isEmpty())
		{
			throw new IllegalArgumentException("must have at least one scheduler");
		}
	}

	@SuppressWarnings("null")
	@Override
	protected Map<Job, Host> selectJobsToDispatch(long time)
	{
		ScheduleCostResult winner = null;
		Iterable<ScheduleCostResult> scheduleResults = scheduleCalculator.calculateSchedule(schedulers, time);
		for (ScheduleCostResult current : scheduleResults)
		{
			double currentCost = current.cost;
			if (shouldReport(time))
			{
				log.info("algo " + current.algorithmName + " cost " + Math.round(currentCost) + " jobs " + current.shceduledJobsToHost.size());
			}
			if (winner == null || currentCost > winner.cost)
			{
				winner = current;
			}
		}
		if (shouldReport(time))
		{
			log.info("selected scheduler " + winner.algorithmName);
		}
		maxCostCollector.collect(time, scheduleResults);
		return winner.shceduledJobsToHost;
	}

}
