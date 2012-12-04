package sim.collectors;

import org.apache.log4j.Logger;

import sim.JobFinisher;
import sim.model.Cluster;

public class IntervalCollector extends Collector
{
	private static final Logger log = Logger.getLogger(IntervalCollector.class);
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

	private String collectLine(long time, boolean handeledEvents, int scheduledJobs)
	{
		HostStatistics hostStatistics = new HostStatistics(cluster);
		waitingQueueStatistics.updateStatistics();
		String line = time + SEPERATOR + hostStatistics.cores() + SEPERATOR + hostStatistics.usedCores() + SEPERATOR + hostStatistics.memory() + SEPERATOR
				+ hostStatistics.usedMemory() + SEPERATOR + hostStatistics.usedMemoryAverage() + SEPERATOR + hostStatistics.usedMemoryVariance() + SEPERATOR
				+ waitingQueueStatistics.waitingJobs() + SEPERATOR + hostStatistics.mixAverage() + SEPERATOR + hostStatistics.mixVariance() + SEPERATOR
				+ hostStatistics.reverseMixAverage() + SEPERATOR + hostStatistics.reverseMixVariance() + SEPERATOR + waitingQueueStatistics.avgMemoryFront()
				+ SEPERATOR + waitingQueueStatistics.avgWaitTimeFront() + SEPERATOR + jobFinisher.collectFinishedJobs() + SEPERATOR
				+ waitingQueueStatistics.dispatchedJobs() + SEPERATOR + waitingQueueStatistics.submittedJobs() + SEPERATOR + scheduledJobs + SEPERATOR
				+ waitingQueueStatistics.waitingJobs();
		if (log.isDebugEnabled())
		{
			log.debug("collectLine() - " + line.replace(' ', ','));
		}
		return line;
	}

	@Override
	protected String collectHeader()
	{
		String line = "#time" + SEPERATOR + "cores" + SEPERATOR + "usedCores" + SEPERATOR + "memory" + SEPERATOR + "usedMemory" + SEPERATOR + "memoryAverage"
				+ SEPERATOR + "memoryVariance" + SEPERATOR + "waitQueueSize" + SEPERATOR + "mixAverage" + SEPERATOR + "mixVariance" + SEPERATOR
				+ "reverseMixAverage" + SEPERATOR + "reverseMixVariance" + SEPERATOR + "avgMemoryFront" + SEPERATOR + "avgWaitTimeFront" + SEPERATOR
				+ "finishedJobs" + SEPERATOR + "dispatchedJobs" + SEPERATOR + "submittedJobs" + SEPERATOR + "scheduledJobs" + SEPERATOR + "waitingJobs";
		return line;
	}

	@Override
	protected String getFileName()
	{
		return MACHINES_UTILIZATION_FILE;
	}

	public void collect(long time, boolean handeledEvents, int scheduledJobs)
	{
		if (shouldCollect(time))
		{
			appendLine(collectLine(time, handeledEvents, scheduledJobs));
		}

	}

	private boolean shouldCollect(long time)
	{
		return time % modulo == 0;
	}
}
