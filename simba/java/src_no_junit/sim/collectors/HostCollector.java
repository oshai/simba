package sim.collectors;

import org.apache.commons.math3.stat.descriptive.moment.Variance;

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
		long memoryAverage = 0;
		Variance memoryVariance = new Variance();
		long mixAverage = 0;
		Variance mixVariance = new Variance();
		for (Host host : cluster.hosts())
		{
			cores += host.cores();
			usedCores += host.usedCores();
			memory += host.memory();
			usedMemory += host.usedMemory();
			memoryAverage += host.usedMemory();
			memoryVariance.increment(host.usedMemory());
			double usedRation = (host.usedMemory() + (host.memory() / 1000)) / (host.usedCores() + (host.cores() / 1000));
			mixAverage += usedRation;
			mixVariance.increment(usedRation);
		}
		mixAverage = mixAverage / cluster.hosts().size();
		memoryAverage = memoryAverage / cluster.hosts().size();
		String line = time + SEPERATOR + cores + SEPERATOR + usedCores + SEPERATOR + memory + SEPERATOR + usedMemory + SEPERATOR + memoryAverage + SEPERATOR
				+ memoryVariance.getResult() + SEPERATOR + waitingQueue.size() + SEPERATOR + mixAverage + SEPERATOR + mixVariance.getResult();
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
