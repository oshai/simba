package sim.collectors;

import java.util.Iterator;

import org.apache.log4j.Logger;

import sim.Clock;
import sim.model.Job;
import sim.scheduling.waiting_queue.WaitingQueueForStatistics;

public class WaitingQueueStatistics
{

	private static final Logger log = Logger.getLogger(WaitingQueueStatistics.class);
	private final WaitingQueueForStatistics waitingQueue;
	private double avgMemoryFront;
	private double avgWaitTimeFront;
	private final int front;
	private final Clock clock;
	private int dispatchedJobs;
	private int submittedJobs;

	public WaitingQueueStatistics(WaitingQueueForStatistics waitingQueue, int front, Clock clock)
	{
		this.front = front;
		this.waitingQueue = waitingQueue;
		this.clock = clock;
	}

	public void updateStatistics()
	{
		Iterator<Job> iterator = waitingQueue.iterator();
		int i = 0;
		double sumMemory = 0;
		double sumWaitTime = 0;
		while (iterator.hasNext() && i < front)
		{
			Job job = iterator.next();
			sumMemory += job.memory();
			sumWaitTime += clock.time() - job.submitTime();
			i++;
		}
		avgMemoryFront = i == 0 ? 0 : sumMemory / i;
		avgWaitTimeFront = i == 0 ? 0 : sumWaitTime / i;
		dispatchedJobs = waitingQueue.collectRemove();
		submittedJobs = waitingQueue.collectAdd();
		if (log.isDebugEnabled())
		{
			log.debug("updateStatistics() - dispatchedJobs" + dispatchedJobs);
			log.debug("updateStatistics() - submittedJobs" + submittedJobs);
		}
	}

	public int waitingJobs()
	{
		return waitingQueue.size();
	}

	public double avgMemoryFront()
	{
		return avgMemoryFront;
	}

	public double avgWaitTimeFront()
	{
		return avgWaitTimeFront;
	}

	public int dispatchedJobs()
	{
		return dispatchedJobs;
	}

	public int submittedJobs()
	{
		return submittedJobs;
	}

}
