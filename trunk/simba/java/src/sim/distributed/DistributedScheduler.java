package sim.distributed;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.log4j.Logger;

import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.Scheduler;
import sim.scheduling.SetWaitingQueue;

public abstract class DistributedScheduler implements Scheduler
{
	private static final Logger log = Logger.getLogger(DistributedScheduler.class);
	public static long VIRUS_TIME = 10;

	protected abstract void distributeJobs(long time);

	protected abstract void scheduleWaitingJobsAgain(long time);

	public static double VIRUS_POWER = 10;
	protected final AbstractWaitingQueue waitingQueue;
	protected final List<HostScheduler> hostSchedulers;
	protected SetWaitingQueue distributedWaitingJobs;

	public DistributedScheduler(AbstractWaitingQueue waitingQueue, List<HostScheduler> hostSchedulers, SetWaitingQueue distributedWaitingJobs)
	{
		super();
		this.waitingQueue = waitingQueue;
		this.hostSchedulers = hostSchedulers;
		this.distributedWaitingJobs = distributedWaitingJobs;
	}

	@Override
	public int schedule(long time)
	{
		long started = System.currentTimeMillis();
		scheduleWaitingJobsAgain(time);
		int waitingJobs = waitingQueue.size();
		distributeJobs(time);
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
		log.info(" wait-jobs end (should be 0) " + waitingQueue.size());
		log.info("schedule -  first job " + waitingQueue.peek());
		int waitingJobsOnHosts = 0;
		int maximumJobsWaitingPerHost = 0;
		int minimumJobsWaitingPerHost = Integer.MAX_VALUE;
		double[] values = new double[hostSchedulers.size()];
		for (int i = 0; i < values.length; i++)
		{
			HostScheduler h = hostSchedulers.get(i);
			int w = h.waitingJobs();
			values[i] = w;
			maximumJobsWaitingPerHost = Math.max(w, maximumJobsWaitingPerHost);
			minimumJobsWaitingPerHost = Math.min(w, minimumJobsWaitingPerHost);
			waitingJobsOnHosts += w;
		}
		// TODO write how many on each host without duplication
		int averageJobsWaitingPerHost = waitingJobsOnHosts / hostSchedulers.size();
		log.info(" wait-jobs on hosts end " + waitingJobsOnHosts);
		log.info(" wait-jobs on hosts end without duplication " + distributedWaitingJobs.size());
		log.info(" max jobs waiting per host " + maximumJobsWaitingPerHost);
		log.info(" min jobs waiting per host " + minimumJobsWaitingPerHost);
		log.info(" averageJobsWaitingPerHost " + averageJobsWaitingPerHost);
		Percentile p = new Percentile();
		p.setData(values);
		log.info(" averageJobsWaitingPerHost " + averageJobsWaitingPerHost);
		for (int i = 10; i < 100; i += 10)
		{
			log.info("precentile " + i + " JobsWaitingPerHost " + p.evaluate(i));

		}

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