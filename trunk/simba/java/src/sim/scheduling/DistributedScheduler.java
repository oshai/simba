package sim.scheduling;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sim.model.Job;

public class DistributedScheduler implements Scheduler
{
	private static final Logger log = Logger.getLogger(DistributedScheduler.class);
	private final AbstractWaitingQueue waitingQueue;
	private final List<HostScheduler> hostSchedulers;
	private final HostSelector hostSelector;

	public DistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, HostSelector hostSelector)
	{
		super();
		this.waitingQueue = waitingQueue;
		this.hostSchedulers = hostSchedulers;
		this.hostSelector = hostSelector;
	}

	@Override
	public int schedule(long time)
	{
		long started = System.currentTimeMillis();
		int waitingJobs = waitingQueue.size();
		distributeJobs();
		int dispatchJobs = dispatch(time);
		if (time % 10800 == 0)
		{
			logScheduler(time, started, dispatchJobs, waitingJobs);
		}
		return dispatchJobs;
	}

	private void logScheduler(long time, long started, int dispatchJobs, int waitingJobs)
	{
		log.info("=============================================");
		log.info("schedule took " + (System.currentTimeMillis() - started));
		log.info("schedule - time " + time + " scheduled jobs " + waitingJobs + " dispatchJobs " + dispatchJobs);
		// " skippedJobs " + skippedJobs);
		// log.info("schedule - avail-hosts start " + startingHostsCount +
		// " avail-host end " + currentCycleHosts.size() + " wait-jobs start " +
		// startingJobsCount
		log.info(" wait-jobs end " + waitingQueue.size());
		log.info("schedule -  first job " + waitingQueue.peek());
		int waitingJobsOnHosts = 0;
		int maximumJobsWaitingPerHost = 0;
		int minimumJobsWaitingPerHost = Integer.MAX_VALUE;
		for (HostScheduler h : hostSchedulers)
		{
			int w = h.waitingJobs();
			maximumJobsWaitingPerHost = Math.max(w, maximumJobsWaitingPerHost);
			minimumJobsWaitingPerHost = Math.min(w, minimumJobsWaitingPerHost);
			waitingJobsOnHosts += w;
		}
		int averageJobsWaitingPerHost = waitingJobsOnHosts / hostSchedulers.size();
		log.info(" wait-jobs on hosts end " + waitingJobsOnHosts);
		log.info(" max jobs waiting per host " + maximumJobsWaitingPerHost);
		log.info(" min jobs waiting per host " + minimumJobsWaitingPerHost);
		log.info(" averageJobsWaitingPerHost " + averageJobsWaitingPerHost);

	}

	private int dispatch(long time)
	{
		int $ = 0;
		for (HostScheduler h : hostSchedulers)
		{
			$ += h.schedule(time);
		}
		return $;
	}

	private void distributeJobs()
	{
		for (Iterator<Job> iterator = waitingQueue.iterator(); iterator.hasNext();)
		{
			Job j = iterator.next();
			HostScheduler hostScheduler = hostSelector.select(j);
			if (null != hostScheduler)
			{
				iterator.remove();
				hostScheduler.addJob(j);
			}
		}
	}

}
