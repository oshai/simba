package sim.distributed;

import static utils.assertions.Asserter.*;

import java.util.List;

import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.Scheduler;
import sim.scheduling.SetWaitingQueue;

public abstract class DistributedScheduler implements Scheduler
{
	private final AbstractWaitingQueue waitingQueue;
	private final List<HostScheduler> hostSchedulers;
	private SetWaitingQueue distributedWaitingJobs;
	private DistributedSchedulerLogger schedulerLogger;

	public DistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, SetWaitingQueue distributedWaitingJobs)
	{
		this.waitingQueue = waitingQueue;
		this.hostSchedulers = hostSchedulers;
		this.distributedWaitingJobs = distributedWaitingJobs;
		schedulerLogger = new DistributedSchedulerLogger(waitingQueue, hostSchedulers, distributedWaitingJobs);
	}

	@Override
	public int schedule(long time)
	{
		long started = System.currentTimeMillis();
		long newJobs = waitingQueue.size();
		int waitingJobs = distributeJobs(time);
		int dispatchJobs = dispatch(time);
		schedulerLogger.log(time, started, newJobs, waitingJobs, dispatchJobs);
		asserter().throwsError().assertFalse(waitingQueue.size() > 0, "waiting queue should always be zero in the end of cycle first waiting job: " + waitingQueue.peek() + " is already waiting on hosts? " + distributedWaitingJobs.contains(waitingQueue.peek()));
		return dispatchJobs;
	}

	protected abstract int distributeJobs(long time);

	protected final SetWaitingQueue distributedWaitingJobs()
	{
		return distributedWaitingJobs;
	}

	protected final AbstractWaitingQueue waitingQueue()
	{
		return waitingQueue;
	}

	protected final List<HostScheduler> hostSchedulers()
	{
		return hostSchedulers;
	}

	protected int dispatch(long time)
	{
		int $ = 0;
		for (HostScheduler h : hostSchedulers)
		{
			$ += h.schedule(time);
		}
		return $;
	}

	protected void addJobToHost(Job j, HostScheduler h)
	{
		h.addJob(j);
		distributedWaitingJobs.add(j);
	}

}