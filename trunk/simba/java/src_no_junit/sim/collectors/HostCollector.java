package sim.collectors;


import sim.model.Cluster;
import sim.model.Host;


public class HostCollector extends Collector<Long>
{
	private static final String MACHINES_UTILIZATION_FILE = "machines_utilization";
	private Cluster cluster;
	private long modulo;
	
	public HostCollector(Cluster cluster, long modulo)
	{
		super();
		this.cluster = cluster;
		this.modulo = modulo;
	}

	@Override
	protected String collectLine(Long time)
	{
		long memory = 0;
		long cores = 0;
		long usedMemory = 0;
		long usedCores = 0;
		
		for (Host host : cluster.hosts())
		{
			cores += host.cores();
			usedCores += host.usedCores();
			memory += host.memory();
			usedMemory += host.usedMemory();
		}
		String line = time + SEPERATOR + 
				cores + SEPERATOR + 
				usedCores + SEPERATOR + 
				memory + SEPERATOR + 
				usedMemory + SEPERATOR;
		return line;
	}

	@Override
	protected String collectHeader()
	{
		String line = "#time" + SEPERATOR + 
				"cores" + SEPERATOR + 
				"usedCores" + SEPERATOR + 
				"memory" + SEPERATOR + 
				"usedMemory" + SEPERATOR;
		return line;
	}

	@Override
	protected String getFileName()
	{
		return MACHINES_UTILIZATION_FILE;
	}
	
	@Override
	protected boolean shouldAppend(Long t)
	{
		return t % modulo == 0;
	}
}
