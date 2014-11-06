package sim.collectors;

import java.util.List;

import sim.scheduling.max_cost.IMaxCostCollector;
import sim.scheduling.reserving.ScheduleCostResult;
import sim.scheduling.waiting_queue.WaitingQueue;

public class MaxCostCollector extends Collector implements IMaxCostCollector
{

	private static final String MAX_COST_FILE_NAME = "max_cost";
	private boolean first = true;

	@Override
	protected String collectHeader()
	{
		return "";
	}

	@Override
	public void collect(long time, Iterable<ScheduleCostResult> results, List<ScheduleCostResult> winner, WaitingQueue waitingQueue)
	{
		if (first)
		{
			String line = "#time" + SEPERATOR + "waiting" + SEPERATOR + "winner" + SEPERATOR + "winnerCost";
			for (ScheduleCostResult scheduleCostResult : results)
			{
				line += SEPERATOR + scheduleCostResult.algorithmName;
			}
			appendLine(line);
			first = false;
		}
		String line = "";
		for (ScheduleCostResult scheduleCostResult : results)
		{
			line += SEPERATOR + scheduleCostResult.cost;
		}
		String winners = "";
		double winnerCost = 0;
		int waitingJobs = 0;
		for (ScheduleCostResult w : winner)
		{
			winners += w.algorithmName.replace(' ', '_') + ',';
			winnerCost = w.cost;
			waitingJobs = waitingQueue.size() - w.shceduledJobsToHost.size();
		}
		appendLine(String.valueOf(time) + SEPERATOR + waitingJobs + SEPERATOR + winners + SEPERATOR + winnerCost + SEPERATOR + line);
	}

	@Override
	protected String getFileName()
	{
		return MAX_COST_FILE_NAME;
	}

}
