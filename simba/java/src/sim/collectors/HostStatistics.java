package sim.collectors;

import org.apache.commons.math3.stat.descriptive.moment.Variance;

import sim.model.Cluster;
import sim.model.Host;
import sim.scheduling.WaitingQueue;

public class HostStatistics
{

	private static final double CONST = 0.01;
	private final Cluster cluster;
	private final WaitingQueue waitingQueue;
	private long memory = 0;
	private long cores = 0;
	private long usedMemory = 0;
	private long usedCores = 0;
	private long usedMemoryAverage = 0;
	private Variance usedMemoryVariance = new Variance();
	private double mixSum = 0;
	private Variance mixVariance = new Variance();
	private double reverseMixSum;
	private Variance reverseMixVariance = new Variance();

	public HostStatistics(Cluster cluster, WaitingQueue waitingQueue)
	{
		this.cluster = cluster;
		this.waitingQueue = waitingQueue;
		if (cluster.hosts().isEmpty())
		{
			throw new IllegalArgumentException("cluster is empty");
		}
		calculate();
	}

	public double mixVariance()
	{
		return mixVariance.getResult();
	}

	public double mixAverage()
	{
		return mixSum / cluster.hosts().size();
	}

	public int waitingJobs()
	{
		return waitingQueue.size();
	}

	public double usedMemoryVariance()
	{
		return usedMemoryVariance.getResult();
	}

	public long usedMemoryAverage()
	{
		return usedMemoryAverage;
	}

	public long usedMemory()
	{
		return usedMemory;
	}

	public long memory()
	{
		return memory;
	}

	public long usedCores()
	{
		return usedCores;
	}

	public long cores()
	{
		return cores;
	}

	private void calculate()
	{
		for (Host host : cluster.hosts())
		{
			cores += host.cores();
			usedCores += host.usedCores();
			memory += host.memory();
			usedMemory += host.usedMemory();
			usedMemoryAverage += host.usedMemory();
			usedMemoryVariance.increment(host.usedMemory());
			double usedMemoryNormalized = (host.usedMemory() / host.memory()) + CONST;
			double usedCoresNormalized = (host.usedCores() / host.cores()) + CONST;
			double usedRatio = usedMemoryNormalized / usedCoresNormalized;
			mixSum += usedRatio;
			mixVariance.increment(usedRatio);
			double availableMemoryNormalized = (host.availableMemory() / host.memory()) + CONST;
			double availableCoresNormalized = (host.availableCores() / host.cores()) + CONST;
			double availableRatio = availableCoresNormalized / availableMemoryNormalized;
			reverseMixSum += availableRatio;
			reverseMixVariance.increment(availableRatio);

		}
		usedMemoryAverage = usedMemoryAverage() / cluster.hosts().size();
	}

	public double reverseMixAverage()
	{
		return reverseMixSum / cluster.hosts().size();
	}

	public double reverseMixVariance()
	{
		return reverseMixVariance.getResult();
	}
}