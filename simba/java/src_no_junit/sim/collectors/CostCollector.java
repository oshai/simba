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
	private CostStatistics costStatistics;
	private List<String> orderedQslots = newArrayList();

	public CostCollector(Cluster cluster, long modulo)
	{
		super();
		this.modulo = modulo;
		Map<String, QslotConfiguration> conf = newHashMap();
		this.costStatistics = new CostStatistics(cluster, conf);
		conf.put("/iil_1base/sdm", new QslotConfiguration("/iil_1base/sdm", 3730.1));
		conf.put("/iil_1base/dt", new QslotConfiguration("/iil_1base/dt", 1866.6));
		conf.put("/iil_1base/wl", new QslotConfiguration("/iil_1base/wl", 1882.4));
		conf.put("/iil_1base/umg", new QslotConfiguration("/iil_1base/umg", 6444.4));
		conf.put("/iil_1base/avl_hsw_core_or", new QslotConfiguration("/iil_1base/avl_hsw_core_or", 200.0));
		conf.put("/iil_1base/jer_cdj", new QslotConfiguration("/iil_1base/jer_cdj", 1978.0));
		conf.put("/iil_1base/admin", new QslotConfiguration("/iil_1base/admin", Double.MAX_VALUE));
		conf.put("/iil_1base/dhg", new QslotConfiguration("/iil_1base/dhg", 349.4));
		conf.put("/iil_1base/lad", new QslotConfiguration("/iil_1base/lad", 8000.0));
		conf.put("/iil_1base/HIP", new QslotConfiguration("/iil_1base/HIP", 1978.0));
		conf.put("/iil_1base/VPG", new QslotConfiguration("/iil_1base/VPG", 400.0));
		conf.put("/iil_1base/mmg", new QslotConfiguration("/iil_1base/mmg", 19920.8));
		conf.put("/iil_1base/ssgi", new QslotConfiguration("/iil_1base/ssgi", 2519.3));
		conf.put("/iil_1base/itec", new QslotConfiguration("/iil_1base/itec", Double.MAX_VALUE));
		conf.put("/iil_1base/CGDG", new QslotConfiguration("/iil_1base/CGDG", 400.0));
		conf.put("/iil_1fast/dhg", new QslotConfiguration("/iil_1fast/dhg", 91.8));
		conf.put("/iil_1fast/training", new QslotConfiguration("/iil_1fast/training", Double.MAX_VALUE));
		conf.put("/iil_1fast/dt", new QslotConfiguration("/iil_1fast/dt", 402.0));
		conf.put("/iil_1fast/lad", new QslotConfiguration("/iil_1fast/lad", 244.8));
		conf.put("/iil_1fast/umg", new QslotConfiguration("/iil_1fast/umg", 1531.4));
		conf.put("/iil_1fast/mmg", new QslotConfiguration("/iil_1fast/mmg", Double.MAX_VALUE));
		conf.put("/iil_1fast/perc", new QslotConfiguration("/iil_1fast/perc", 70.0));
		conf.put("/iil_1fast/dt_delta", new QslotConfiguration("/iil_1fast/dt_delta", 81.6));
		conf.put("/iil_1fast/tmp_test_ppv", new QslotConfiguration("/iil_1fast/tmp_test_ppv", Double.MAX_VALUE));
		conf.put("/iil_1fast/admin", new QslotConfiguration("/iil_1fast/admin", Double.MAX_VALUE));
		conf.put("/iil_1_s/arch_benchmark", new QslotConfiguration("/iil_1_s/arch_benchmark", Double.MAX_VALUE));
		conf.put("/iil_1_s/arch_or", new QslotConfiguration("/iil_1_s/arch_or", Double.MAX_VALUE));
		conf.put("/iil_1_s/ppa", new QslotConfiguration("/iil_1_s/ppa", Double.MAX_VALUE));
		conf.put("/iil_1_s/arch", new QslotConfiguration("/iil_1_s/arch", 3000.0));
		conf.put("/iil_1_s/avl_hsw_core_or", new QslotConfiguration("/iil_1_s/avl_hsw_core_or", Double.MAX_VALUE));
		conf.put("/iil_1_s/pdx_vpool", new QslotConfiguration("/iil_1_s/pdx_vpool", Double.MAX_VALUE));
		conf.put("/iil_1_s/admin", new QslotConfiguration("/iil_1_s/admin", Double.MAX_VALUE));

	}

	private String collectLine(long time)
	{
		Map<String, Qslot> statistics = costStatistics.apply();
		String line = "";
		double totalCost = 0.0;
		double totalMaxCost = 0.0;
		double totalErrorCost = 0.0;
		for (String qslotName : orderedQslots)
		{
			double cost = statistics.get(qslotName).cost();
			double maxCost = statistics.get(qslotName).maxCost();
			double costError = statistics.get(qslotName).costError();
			line += SEPERATOR + cost;
			line += SEPERATOR + maxCost;
			line += SEPERATOR + costError;
			totalCost += cost;
			totalMaxCost += maxCost;
			totalErrorCost += costError;
		}
		line = String.valueOf(time) + SEPERATOR + totalCost + SEPERATOR + totalMaxCost + SEPERATOR + totalErrorCost + SEPERATOR + line;
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
		String line = "#time" + SEPERATOR + "totalCost" + SEPERATOR + "totalMaxCost" + SEPERATOR + "totalErrorCost";
		for (String qslotName : orderedQslots)
		{
			line += SEPERATOR + "running-cost_of_" + qslotName;
			line += SEPERATOR + "max-cost_of_" + qslotName;
			line += SEPERATOR + "cost-error_of_" + qslotName;
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
