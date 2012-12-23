package sim.collectors;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sim.model.Cluster;
import sim.scheduling.WaitingQueueForStatistics;

public class CostCollector extends Collector implements IntervalCollector
{
	private static final Logger log = Logger.getLogger(CostCollector.class);
	private static final String COST_COLLECTOR_FILE = "fairshare_trace";
	private long modulo;
	private CostStatistics costStatistics;
	private List<String> orderedQslots = newArrayList();

	public CostCollector(Cluster cluster, long modulo, WaitingQueueForStatistics waitingQueue)
	{
		super();
		this.modulo = modulo;
		Map<String, QslotConfiguration> conf = newHashMap();
		this.costStatistics = new CostStatistics(cluster, conf, waitingQueue);

	}

	private String collectLine(long time)
	{
		Map<String, Qslot> statistics = costStatistics.apply();
		String line = "";
		double totalCost = 0.0;
		double totalAbsoluteShouldGetError = 0.0;
		double totalRelativeRunningShouldGetError = 0.0;
		double totalRelativeWaitingShouldGetError = 0.0;
		for (String qslotName : orderedQslots)
		{
			double cost = statistics.get(qslotName).cost();
			// double maxCost = statistics.get(qslotName).maxCost();
			// double costError = statistics.get(qslotName).costError();
			double absoluteShouldGetError = statistics.get(qslotName).absoluteShouldGetError();
			double relativeRunningShouldGetError = statistics.get(qslotName).relativeRunningShouldGetError();
			double relativeWaitingShouldGetError = statistics.get(qslotName).relativeWaitingShouldGetError();
			line += SEPERATOR + cost;
			line += SEPERATOR + absoluteShouldGetError;
			line += SEPERATOR + relativeRunningShouldGetError;
			line += SEPERATOR + relativeWaitingShouldGetError;
			totalCost += cost;
			totalAbsoluteShouldGetError += absoluteShouldGetError;
			totalRelativeRunningShouldGetError += relativeRunningShouldGetError;
			totalRelativeWaitingShouldGetError += relativeWaitingShouldGetError;
		}
		line = String.valueOf(time) + SEPERATOR + totalCost + SEPERATOR + totalAbsoluteShouldGetError + SEPERATOR + totalRelativeRunningShouldGetError + SEPERATOR + totalRelativeWaitingShouldGetError + SEPERATOR + line;
		if (log.isDebugEnabled())
		{
			log.debug("collectLine() - " + line.replace(' ', ','));
		}
		return line;
	}

	@Override
	protected String collectHeader()
	{
		Map<String, Qslot> statistics = costStatistics.apply();
		orderedQslots.addAll(statistics.keySet());
		Collections.sort(orderedQslots);
		String line = "#time" + SEPERATOR + "totalCost" + SEPERATOR + "totalAbsoluteShouldGetError" + SEPERATOR + "totalRelativeRunningShouldGetError" + SEPERATOR + "totalRelativeWaitingShouldGetError";
		for (String qslotName : orderedQslots)
		{
			line += SEPERATOR + "running-cost_of_" + qslotName;
			line += SEPERATOR + "absoluteShouldGetError_of_" + qslotName;
			line += SEPERATOR + "relativeRunningShouldGetError_of_" + qslotName;
			line += SEPERATOR + "relativeWaitingShouldGetError_of_" + qslotName;
		}
		return line;
	}

	@Override
	protected String getFileName()
	{
		return COST_COLLECTOR_FILE;
	}

	public void collect(long time, boolean handeledEvents, int scheduledJobs)
	{
		if (shouldCollect(time))
		{
			appendLine(collectLine(time));
		}

	}

	private boolean shouldCollect(long time)
	{
		return time % modulo == 0;
	}
}
