package sim.parsers;

import java.io.File;

import org.apache.log4j.Logger;

import sim.model.Cluster;
import sim.model.Host;
import utils.TextFileUtils;

public class HostParser
{
	private static final Logger log = Logger.getLogger(HostParser.class);
	private static final String HOST_FILE = System.getProperty("hosts-file");
	private static final double HOST_MEMORY_MULTIPLIER = getMultiplier();
	private static final int HOST_MEMORY_UNIT_NORMILIZER = Integer.getInteger("hosts-memory-noralize");

	private static final int index_hostid = 0;
	private static final int index_cores = 1;
	private static final int index_memory = 2;

	public Cluster parse()
	{
		log.info("parse() - starting with file " + HOST_FILE);
		log.info("parse() - memory multiplier is " + HOST_MEMORY_MULTIPLIER);
		int dropped = 0;
		Cluster cluster = new Cluster();
		String contents = TextFileUtils.getContents(new File(HOST_FILE));
		String[] lines = contents.split("\n");
		for (String line : lines)
		{
			try
			{
				String[] cols = line.split(",");
				cluster.add(Host.create().id(cols[index_hostid]).cores(Double.valueOf(cols[index_cores]))
						.memory(HOST_MEMORY_MULTIPLIER * Double.valueOf(cols[index_memory]) / HOST_MEMORY_UNIT_NORMILIZER).build());
			}
			catch (Exception ex)
			{
				log.debug("parse() - fail on line " + line);
				dropped++;
			}
		}
		log.info("parse() - dropped " + dropped + " which is: " + (int) ((double) dropped * 100 / (cluster.hosts().size() + dropped)) + "%");
		return cluster;
	}

	private static double getMultiplier()
	{
		return Double.valueOf(System.getProperty("host-memory-multiplier", "1.0"));
	}
}
