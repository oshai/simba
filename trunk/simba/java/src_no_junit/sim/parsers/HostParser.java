package sim.parsers;

import java.io.File;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import sim.SimbaConfiguration;
import sim.model.Cluster;
import sim.model.Host;
import utils.TextFileUtils;

public class HostParser
{
	private static final Logger log = Logger.getLogger(HostParser.class);
	private final String HOST_FILE = System.getProperty("hosts-file");
	private final int HOST_MEMORY_UNIT_NORMILIZER = Integer.getInteger("hosts-memory-noralize");

	private static final int index_hostid = 0;
	private static final int index_cores = 1;
	private static final int index_memory = 2;

	private final SimbaConfiguration simbaConfiguration;
	private final Cluster cluster;

	@Inject
	public HostParser(SimbaConfiguration simbaConfiguration, Cluster cluster)
	{
		super();
		this.simbaConfiguration = simbaConfiguration;
		this.cluster = cluster;
	}

	public void parse()
	{
		log.info("parse() - starting with file " + HOST_FILE);
		log.info("parse() - memory multiplier is " + simbaConfiguration.hostMemoryRatio());
		int dropped = 0;
		int hosts = 0;
		String contents = TextFileUtils.getContents(new File(HOST_FILE));
		String[] lines = contents.split("\n");
		for (String line : lines)
		{
			try
			{
				String[] cols = line.split(",");
				double configuredMemory = Double.valueOf(cols[index_memory]) / HOST_MEMORY_UNIT_NORMILIZER;
				double memory = Math.round(simbaConfiguration.hostMemoryRatio() * configuredMemory);
				if (shouldAdd(hosts))
				{
					double configuredCores = Double.valueOf(cols[index_cores]);
					double core = simbaConfiguration.hostCoreRatio() * configuredCores;
					cluster.add(Host.builder().id(cols[index_hostid]).cores(core).memory(memory).build());
				}
				else
				{
					dropped++;
				}
				hosts++;
			}
			catch (Exception ex)
			{
				log.debug("parse() - fail on line " + line);
				dropped++;
			}
		}
		// for (Host host : cluster.hosts())
		// {
		// log.info("host - " + host);
		// }
		log.info("parse() - dropped " + dropped + " which is: " + (int) ((double) dropped * 100 / (cluster.hosts().size() + dropped)) + "%");
	}

	private boolean shouldAdd(int hosts)
	{
		return hosts % 100 < (simbaConfiguration.machineDropRatio() * 100);
	}
}
