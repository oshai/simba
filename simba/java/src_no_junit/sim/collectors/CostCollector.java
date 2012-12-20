package sim.collectors;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sim.model.Cluster;

public class CostCollector extends Collector implements IntervalCollector
{
	private static final Logger log = Logger.getLogger(CostCollector.class);
	private static final String COST_COLLECTOR_FILE = "fairshare_trace";
	private long modulo;
	CostStatistics costStatistics;
	List<String> orderedQslots = newArrayList();

	public CostCollector(Cluster cluster, long modulo)
	{
		super();
		this.modulo = modulo;
		Map<String, QslotConfiguration> conf = newHashMap();
		costStatistics = new CostStatistics(cluster, conf);
	}

	private String collectLine(long time)
	{
		Map<String, Qslot> statistics = costStatistics.apply();
		String line = String.valueOf(time);
		for (String qslotName : orderedQslots)
		{
			line += SEPERATOR + statistics.get(qslotName).cost();
			line += SEPERATOR + statistics.get(qslotName).maxCost();
		}
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
		String line = "#time";
		for (String qslotName : orderedQslots)
		{
			line += SEPERATOR + "running-cost_of_" + qslotName;
			line += SEPERATOR + "max-cost_of_" + qslotName;
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
