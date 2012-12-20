package sim.collectors;

import static utils.assertions.Asserter.*;

import java.util.Map;
import java.util.Map.Entry;

import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;

import com.google.common.collect.Maps;

public class CostStatistics
{
	private final Cluster cluster;
	private final Map<String, QslotConfiguration> configuration;

	public CostStatistics(Cluster cluster, Map<String, QslotConfiguration> configuration)
	{
		this.cluster = cluster;
		this.configuration = configuration;
	}

	public Map<String, Qslot> apply()
	{
		Map<String, Qslot> $ = Maps.newHashMap();
		for (Entry<String, QslotConfiguration> e : configuration.entrySet())
		{
			$.put(e.getKey(), new Qslot(e.getValue()));
		}
		for (Host h : cluster.hosts())
		{
			for (Job j : h.jobs())
			{
				Qslot qslot = $.get(j.qslot());
				asserter().assertNotNull(qslot, "didnt find qslot " + j.qslot());
				qslot.addCost(j.cost());
			}
		}
		return $;
	}

}
