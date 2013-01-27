package sim.collectors;

import java.util.List;

import sim.scheduling.max_cost.IMaxCostCollector;
import sim.scheduling.reserving.ScheduleCostResult;

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
	public void collect(long time, Iterable<ScheduleCostResult> results, List<ScheduleCostResult> winner)
	{
		if (first)
		{
			String line = "#time" + SEPERATOR + "winner";
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
		for (ScheduleCostResult w : winner)
		{
			winners += w.algorithmName.replace(' ', '_') + ',';
		}
		appendLine(String.valueOf(time) + SEPERATOR + winners + SEPERATOR + line);
	}

	@Override
	protected String getFileName()
	{
		return MAX_COST_FILE_NAME;
	}

}
