package sim.parsers;

import java.io.File;

import org.apache.log4j.Logger;

import sim.model.Cluster;
import sim.model.Host;
import utils.TextFileUtils;

public class CopyOfHostParser
{
	private static final Logger log = Logger.getLogger(CopyOfHostParser.class);
	private static final String HOST_FILE = "/tmp/iil1_workstations";

	public Cluster parse()
	{
		log.info("parse() - starting with file " + HOST_FILE);
		int dropped = 0;
		Cluster cluster = new Cluster();
		String contents = TextFileUtils.getContents(new File(HOST_FILE));
		String[] lines = contents.split("\n");
		for (String line : lines)
		{
			try
			{
				String[] cols = line.split(",");
				cluster.add(Host.create().id(cols[0]).cores(Double.valueOf(cols[1])).memory(Double.valueOf(cols[2])).build());
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
}
