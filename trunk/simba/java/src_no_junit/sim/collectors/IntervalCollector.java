package sim.collectors;

import sim.JobFinisher;
import sim.model.Cluster;

public class IntervalCollector extends Collector<Long>
{
	private static final String MACHINES_UTILIZATION_FILE = "machines_utilization";
	private Cluster cluster;
	private long modulo;
	private final WaitingQueueStatistics waitingQueueStatistics;
	private final JobFinisher jobFinisher;

	public IntervalCollector(Cluster cluster, long modulo, WaitingQueueStatistics waitingQueueStatistics, JobFinisher jobFinisher)
	{
		super();
		this.cluster = cluster;
		this.modulo = modulo;
		this.waitingQueueStatistics = waitingQueueStatistics;
		this.jobFinisher = jobFinisher;
	}

	@Override
	protected String collectLine(Long time)
	{
		HostStatistics hostStatistics = new HostStatistics(cluster);
		waitingQueueStatistics.updateStatistics();
		String line = time + SEPERATOR + hostStatistics.cores() + SEPERATOR + hostStatistics.usedCores() + SEPERATOR + hostStatistics.memory() + SEPERATOR
				+ hostStatistics.usedMemory() + SEPERATOR + hostStatistics.usedMemoryAverage() + SEPERATOR + hostStatistics.usedMemoryVariance() + SEPERATOR
				+ waitingQueueStatistics.waitingJobs() + SEPERATOR + hostStatistics.reverseMixAverage() + SEPERATOR + hostStatistics.reverseMixVariance()
				+ SEPERATOR + waitingQueueStatistics.avgMemoryFront() + SEPERATOR + waitingQueueStatistics.avgWaitTimeFront() + SEPERATOR
				+ jobFinisher.collectFinishedJobs() + SEPERATOR + waitingQueueStatistics.dispatchedJobs() + SEPERATOR + waitingQueueStatistics.submittedJobs();
		return line;
	}

	@Override
	protected String collectHeader()
	{
		String line = "#time" + SEPERATOR + "cores" + SEPERATOR + "usedCores" + SEPERATOR + "memory" + SEPERATOR + "usedMemory" + SEPERATOR + "memoryAverage"
				+ SEPERATOR + "memoryVariance" + SEPERATOR + "waitQueueSize" + SEPERATOR + "mixAverage" + SEPERATOR + "mixVariance" + SEPERATOR
				+ "reverseMixAverage" + SEPERATOR + "reverseMixVariance" + SEPERATOR + "avgMemoryFront" + SEPERATOR + "avgWaitTimeFront" + SEPERATOR
				+ "finishedJobs" + SEPERATOR + "dispatchedJobs" + SEPERATOR + "submittedJobs";
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
