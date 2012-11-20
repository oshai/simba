package sim.collectors;

import java.util.Iterator;

import sim.Clock;
import sim.model.Job;
import sim.scheduling.WaitingQueue;

public class WaitingQueueStatistics
{

	private final WaitingQueue waitingQueue;
	private double avgMemoryFront;
	private double avgWaitTimeFront;
	private final int front;
	private final Clock clock;
	private int dispatchedJobs;
	private int submittedJobs;

	public WaitingQueueStatistics(WaitingQueue waitingQueue, int front, Clock clock)
	{
		this.front = front;
		this.waitingQueue = waitingQueue;
		this.clock = clock;
	}

	public void updateStatistics()
	{
		if (waitingQueue.isEmpty())
		{
			return;
		}
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
		avgMemoryFront = sumMemory / i;
		avgWaitTimeFront = sumWaitTime / i;
		dispatchedJobs = waitingQueue.collectRemove();
		submittedJobs = waitingQueue.collectAdd();
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
