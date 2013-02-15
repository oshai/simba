package sim.parsers;

import javax.inject.Inject;

import sim.SimbaConfiguration;
import sim.model.Cluster;
import sim.model.Host;

public class HostParserFixed implements IHostParser
{
	private final Cluster cluster;

	@Inject
	public HostParserFixed(SimbaConfiguration simbaConfiguration, Cluster cluster)
	{
		super();
		this.cluster = cluster;
	}

	public void parse()
	{
		for (int i = 0; i < 64; i++)
		{
			cluster.add(Host.builder().id("id" + i).cores(8).memory(64).build());
		}
	}
}
