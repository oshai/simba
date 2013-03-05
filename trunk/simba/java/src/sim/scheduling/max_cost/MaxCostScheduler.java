package sim.scheduling.max_cost;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import sim.SimbaConfiguration;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.JobDispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.graders.Grader;
import sim.scheduling.reserving.ReservingScheduler;
import sim.scheduling.reserving.ScheduleCostResult;
import sim.scheduling.waiting_queue.WaitingQueue;

public class MaxCostScheduler extends ReservingScheduler implements Scheduler
{
	private static final Logger log = Logger.getLogger(MaxCostScheduler.class);
	private final List<ReservingScheduler> schedulers;
	private final Map<String, Double> sumOfCostGainInWinsOverBestFit;
	private final Map<String, Integer> numOfWinsOverBestFit;
	private final Map<String, Integer> numOfWins;
	private final ScheduleCalculator scheduleCalculator;
	private final IMaxCostCollector maxCostCollector;

	@Inject
	public MaxCostScheduler(WaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher, SimbaConfiguration simbaConfiguration, List<ReservingScheduler> schedulers, ScheduleCalculator scheduleCalculator, IMaxCostCollector maxCostCollector)
	{
		super(waitingQueue, cluster, grader, dispatcher, simbaConfiguration);
		this.schedulers = schedulers;
		this.scheduleCalculator = scheduleCalculator;
		this.maxCostCollector = maxCostCollector;
		if (schedulers.isEmpty())
		{
			throw new IllegalArgumentException("must have at least one scheduler");
		}
		// TODO need to assert best fit is first
		sumOfCostGainInWinsOverBestFit = newHashMap();
		numOfWinsOverBestFit = newHashMap();
		numOfWins = newHashMap();
		for (ReservingScheduler reservingScheduler : schedulers)
		{
			sumOfCostGainInWinsOverBestFit.put(reservingScheduler.grader().toString(), 0.0);
			numOfWinsOverBestFit.put(reservingScheduler.grader().toString(), 0);
			numOfWins.put(reservingScheduler.grader().toString(), 0);
		}

	}

	@Override
	protected Map<Job, Host> selectJobsToDispatch(long time)
	{
		List<ScheduleCostResult> winner = newArrayList();
		Iterable<ScheduleCostResult> scheduleResults = scheduleCalculator.calculateSchedule(schedulers, time);
		for (ScheduleCostResult current : scheduleResults)
		{
			double currentCost = current.cost;
			if (shouldReport(time))
			{
				log.info("algo " + current.algorithmName + " cost " + Math.round(currentCost) + " jobs " + current.shceduledJobsToHost.size());
			}
			if (winner.isEmpty() || currentCost > winner.get(0).cost)
			{
				winner = newArrayList();
				winner.add(current);
			}
			else if (Double.compare(winner.get(0).cost, currentCost) == 0)
			{
				winner.add(current);
			}
			ScheduleCostResult bestFit = scheduleResults.iterator().next();
			if (currentCost > bestFit.cost)
			{
				sumOfCostGainInWinsOverBestFit.put(current.algorithmName, (currentCost - bestFit.cost) + sumOfCostGainInWinsOverBestFit.get(current.algorithmName));
				numOfWinsOverBestFit.put(current.algorithmName, 1 + numOfWinsOverBestFit.get(current.algorithmName));
			}
		}
		for (ScheduleCostResult scheduleCostResult : winner)
		{
			numOfWins.put(scheduleCostResult.algorithmName, 1 + numOfWins.get(scheduleCostResult.algorithmName));
		}
		if (shouldReport(time))
		{
			log.info("selected schedulers " + winner);
			log.info("sumOfCostGainInWinsOverBestFit " + sumOfCostGainInWinsOverBestFit);
			log.info("numOfWinsOverBestFit " + numOfWinsOverBestFit);
			log.info("numOfWins " + numOfWins);
		}
		maxCostCollector.collect(time, scheduleResults, winner, waitingQueue());
		return winner.get(0).shceduledJobsToHost;
	}

}
