package sim.collectors;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import sim.JobFinisher;
import sim.SimbaConfiguration;
import sim.model.Cluster;

public class MiscStatisticsCollector extends Collector implements IntervalCollector
{
	private static final Logger log = Logger.getLogger(MiscStatisticsCollector.class);
	private static final String MACHINES_UTILIZATION_FILE = "machines_utilization";
	private final Cluster cluster;
	private final WaitingQueueStatistics waitingQueueStatistics;
	private final JobFinisher jobFinisher;
	private final SimbaConfiguration simbaConfiguration;

	@Inject
	public MiscStatisticsCollector(Cluster cluster, WaitingQueueStatistics waitingQueueStatistics, JobFinisher jobFinisher, SimbaConfiguration simbaConfiguration)
	{
		super();
		this.cluster = cluster;
		this.waitingQueueStatistics = waitingQueueStatistics;
		this.jobFinisher = jobFinisher;
		this.simbaConfiguration = simbaConfiguration;
	}

	private String collectLine(long time, boolean handeledEvents, int scheduledJobs)
	{
		HostStatistics hostStatistics = new HostStatistics(cluster);
		waitingQueueStatistics.updateStatistics();
		String line = time + SEPERATOR + hostStatistics.cores() + SEPERATOR + hostStatistics.usedCores() + SEPERATOR + hostStatistics.memory() + SEPERATOR + hostStatistics.usedMemory() + SEPERATOR + waitingQueueStatistics.waitingJobs() + SEPERATOR + hostStatistics.usedCost() + SEPERATOR + waitingQueueStatistics.avgMemoryFront() + SEPERATOR + waitingQueueStatistics.avgWaitTimeFront() + SEPERATOR + jobFinisher.collectFinishedJobs() + SEPERATOR + waitingQueueStatistics.dispatchedJobs() + SEPERATOR + waitingQueueStatistics.submittedJobs() + SEPERATOR + scheduledJobs;
		if (log.isDebugEnabled())
		{
			log.debug("collectLine() - " + line.replace(' ', ','));
		}
		return line;
	}

	@Override
	protected String collectHeader()
	{
		String line = "#time" + SEPERATOR + "cores" + SEPERATOR + "usedCores" + SEPERATOR + "memory" + SEPERATOR + "usedMemory" + SEPERATOR + "waitingJobs" + SEPERATOR + "usedCost" + SEPERATOR + "avgMemoryWaitingJobs" + SEPERATOR + "avgWaitTimeWaitingJobs" + SEPERATOR + "finishedJobs" + SEPERATOR + "dispatchedJobs" + SEPERATOR + "submittedJobs" + SEPERATOR + "scheduledJobs";
		return line;
	}

	@Override
	protected String getFileName()
	{
		return MACHINES_UTILIZATION_FILE;
	}

	@Override
	public void collect(long time, boolean handeledEvents, int scheduledJobs)
	{
		if (shouldCollect(time))
		{
			appendLine(collectLine(time, handeledEvents, scheduledJobs));
		}

	}

	private boolean shouldCollect(long time)
	{
		return time % simbaConfiguration.collectTime() == 0;
	}
}
