package sim.collectors;

import sim.model.Cluster;
import sim.model.Host;
import sim.scheduling.WaitingQueue;

public class HostCollector extends Collector<Long>
{
	private static final String MACHINES_UTILIZATION_FILE = "machines_utilization";
	private Cluster cluster;
	private long modulo;
	private final WaitingQueue waitingQueue;

	public HostCollector(Cluster cluster, long modulo, WaitingQueue waitingQueue)
	{
		super();
		this.cluster = cluster;
		this.modulo = modulo;
		this.waitingQueue = waitingQueue;
	}

	@Override
	protected String collectLine(Long time)
	{
		long memory = 0;
		long cores = 0;
		long usedMemory = 0;
		long usedCores = 0;
		long averageMemory = 0;

		for (Host host : cluster.hosts())
		{
			cores += host.cores();
			usedCores += host.usedCores();
			memory += host.memory();
			usedMemory += host.usedMemory();
			averageMemory += host.usedMemory();
		}
		averageMemory = averageMemory / cluster.hosts().size();
		String line = time + SEPERATOR + cores + SEPERATOR + usedCores + SEPERATOR + memory + SEPERATOR + usedMemory + SEPERATOR + averageMemory + SEPERATOR
				+ waitingQueue.size() + SEPERATOR;
		return line;
	}

	@Override
	protected String collectHeader()
	{
		String line = "#time" + SEPERATOR + "cores" + SEPERATOR + "usedCores" + SEPERATOR + "memory" + SEPERATOR + "usedMemory" + SEPERATOR + "averageMemory"
				+ SEPERATOR + "waitQueueSize" + SEPERATOR;
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
