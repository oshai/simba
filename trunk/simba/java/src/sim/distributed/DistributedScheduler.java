package sim.distributed;

import static utils.assertions.Asserter.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.log4j.Logger;

import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.Scheduler;
import sim.scheduling.SetWaitingQueue;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public abstract class DistributedScheduler implements Scheduler
{
	private static final Logger log = Logger.getLogger(DistributedScheduler.class);
	private final AbstractWaitingQueue waitingQueue;
	private final List<HostScheduler> hostSchedulers;
	private SetWaitingQueue distributedWaitingJobs;

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
		long newJobs = waitingQueue.size();
		int waitingJobs = distributeJobs(time);
		int dispatchJobs = dispatch(time);
		if (shouldLog(time, started))
		{
			logScheduler(time, started, dispatchJobs, waitingJobs, newJobs);
		}
		asserter().throwsError().assertFalse(waitingQueue.size() > 0, "waiting queue should always be zero in the end of cycle first waiting job: " + waitingQueue.peek() + " is already waiting on hosts? " + distributedWaitingJobs.contains(waitingQueue.peek()));
		return dispatchJobs;
	}

	private boolean shouldLog(long time, long started)
	{
		return time % TimeUnit.HOURS.toSeconds(3) == 0 || (System.currentTimeMillis() - started > TimeUnit.SECONDS.toMillis(10));
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

	private void logScheduler(long time, long started, int dispatchJobs, int waitingJobs, long newJobs)
	{
		log.info("=============================================");
		log.info("schedule took " + (System.currentTimeMillis() - started));
		log.info("schedule - time " + time + " scheduled jobs " + waitingJobs + " dispatchJobs " + dispatchJobs);
		// " skippedJobs " + skippedJobs);
		// log.info("schedule - avail-hosts start " + startingHostsCount +
		// " avail-host end " + currentCycleHosts.size() + " wait-jobs start " +
		// startingJobsCount
		log.info(" wait-jobs new submitted jobs " + newJobs);
		log.info(" wait-jobs end (should be 0) " + waitingQueue.size());
		log.info("schedule -  first job " + waitingQueue.peek());
		logHosts();
		logJobs(time);

	}

	private void logJobs(long time)
	{
		double[] valuesJobMemory = new double[distributedWaitingJobs.size()];
		double[] valuesJobCore = new double[distributedWaitingJobs.size()];
		double[] valuesJobWait = new double[distributedWaitingJobs.size()];
		Job maxWaitingJobJob = null;
		Iterator<Job> iterator = distributedWaitingJobs.iterator();
		for (int i = 0; i < valuesJobWait.length; i++)
		{
			Job job = iterator.next();
			valuesJobCore[i] = job.cores();
			valuesJobMemory[i] = job.memory();
			valuesJobWait[i] = time - job.submitTime();
			if (maxWaitingJobJob == null || maxWaitingJobJob.submitTime() > job.submitTime())
			{
				maxWaitingJobJob = job;
			}
		}
		logPrecentile(valuesJobMemory, "jobs", "memory");
		logPrecentile(valuesJobCore, "jobs", "cores");
		logPrecentile(valuesJobWait, "jobs", "wait-time");
		log.info("max waiting job is " + maxWaitingJobJob);
	}

	private void logHosts()
	{
		int waitingJobsOnHosts = 0;
		int maximumJobsWaitingPerHost = 0;
		int minimumJobsWaitingPerHost = Integer.MAX_VALUE;
		double[] values = new double[hostSchedulers.size()];
		Multimap<Job, HostScheduler> jobsForHosts = HashMultimap.create();
		for (int i = 0; i < values.length; i++)
		{
			HostScheduler h = hostSchedulers.get(i);
			int w = h.waitingJobsSize();
			values[i] = w;
			maximumJobsWaitingPerHost = Math.max(w, maximumJobsWaitingPerHost);
			minimumJobsWaitingPerHost = Math.min(w, minimumJobsWaitingPerHost);
			waitingJobsOnHosts += w;
			for (Job j : h.waitingJobs())
			{
				jobsForHosts.put(j, h);
			}
		}
		int averageJobsWaitingPerHost = waitingJobsOnHosts / hostSchedulers.size();
		log.info(" wait-jobs on hosts end " + waitingJobsOnHosts);
		log.info(" wait-jobs on hosts end without duplication " + distributedWaitingJobs.size());
		log.info(" max jobs waiting per host " + maximumJobsWaitingPerHost);
		log.info(" min jobs waiting per host " + minimumJobsWaitingPerHost);
		log.info(" averageJobsWaitingPerHost " + averageJobsWaitingPerHost);
		logPrecentile(values, "hosts", "jobs");
		logJobsDistribution(jobsForHosts);
	}

	private void logJobsDistribution(Multimap<Job, HostScheduler> jobsForHosts)
	{
		double[] jobsForHostsDist = new double[jobsForHosts.size()];
		Iterator<Collection<HostScheduler>> iterator = jobsForHosts.asMap().values().iterator();
		for (int i = 0; i < jobsForHostsDist.length; i++)
		{
			jobsForHostsDist[i] = iterator.next().size();
		}
		logPrecentile(jobsForHostsDist, "jobs", "hosts");
	}

	private void logPrecentile(double[] values, String key, String value)
	{
		Percentile p2 = new Percentile();
		p2.setData(values);
		for (int i = 10; i <= 100; i += 10)
		{
			log.info("" + i + "% of " + key + " has less than " + p2.evaluate(i) + " " + value);
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