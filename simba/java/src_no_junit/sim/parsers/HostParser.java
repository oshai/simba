package sim.parsers;

import java.io.File;

import org.apache.log4j.Logger;

import sim.model.Cluster;
import sim.model.Host;

import com.intel.swiss.sws.mechanism.file.TextFileUtil;

public class HostParser
{
	private static final Logger log = Logger.getLogger(HostParser.class);
	private static final String HOST_FILE = "/tmp/iil1_workstations";
	
	public Cluster parse()
	{
		Cluster cluster = new Cluster();
		String contents = TextFileUtil.getContents(new File(HOST_FILE));
		String[] lines = contents.split("\n");
		for (String line : lines)
		{
			try
			{
				String[] cols = line.split(",");
				cluster.add(Host.Builder.create().id(cols[0]).cores(Double.valueOf(cols[1])).memory(Double.valueOf(cols[2])).build());
			}
			catch (Exception ex)
			{
				log.warn("parse() - fail on line " + line);
			}
		}
		return cluster;
	}
}
