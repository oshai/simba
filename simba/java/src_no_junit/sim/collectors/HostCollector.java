package sim.collectors;

import sim.model.Cluster;
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
		HostStatistics statistics = new HostStatistics(cluster, waitingQueue);
		String line = time
				+ SEPERATOR
				+ (statistics.cores() + SEPERATOR + statistics.usedCores() + SEPERATOR + statistics.memory() + SEPERATOR + statistics.usedMemory() + SEPERATOR + statistics.usedMemoryAverage() + SEPERATOR
						+ statistics.usedMemoryVariance() + SEPERATOR + statistics.waitingJobs() + SEPERATOR + statistics.mixAverage() + SEPERATOR + statistics.mixVariance());
		return line;
	}

	@Override
	protected String collectHeader()
	{
		String line = "#time" + SEPERATOR + "cores" + SEPERATOR + "usedCores" + SEPERATOR + "memory" + SEPERATOR + "usedMemory" + SEPERATOR + "memoryAverage"
				+ SEPERATOR + "memoryVariance" + SEPERATOR + "waitQueueSize" + SEPERATOR + "mixAverage" + SEPERATOR + "mixVariance";
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
