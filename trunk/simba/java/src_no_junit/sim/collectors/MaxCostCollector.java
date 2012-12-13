package sim.collectors;

import sim.scheduling.reserving.IMaxCostCollector;
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
	public void collect(long time, Iterable<ScheduleCostResult> results)
	{
		if (first)
		{
			String line = "#time";
			for (ScheduleCostResult scheduleCostResult : results)
			{
				line += SEPERATOR + scheduleCostResult.algorithmName;
			}
			appendLine(line);
			first = false;
		}
		String line = String.valueOf(time);
		for (ScheduleCostResult scheduleCostResult : results)
		{
			line += SEPERATOR + scheduleCostResult.cost;
		}
		appendLine(line);
	}

	@Override
	protected String getFileName()
	{
		return MAX_COST_FILE_NAME;
	}

}
