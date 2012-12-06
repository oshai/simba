package sim.scheduling.reserving;

import static com.google.common.collect.Lists.*;
import static utils.assertions.Asserter.*;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import sim.SimbaConfiguration;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.JobDispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.graders.Grader;

public class ReservingScheduler implements Scheduler
{
	private static final Logger log = Logger.getLogger(ReservingScheduler.class);
	private final Job DUMMY_JOB;
	private static final Host DUMMY_HOST = Host.builder().id("dummy").cores(0).memory(0).build();
	private final AbstractWaitingQueue waitingQueue;
	private final Cluster cluster;
	private final Grader grader;
	private final JobDispatcher dispatcher;
	private ReservationsHolderSupplier reservationsSupplier;
	private List<Host> currentCycleHosts;
	private double maxAvailableMemory;
	private double maxAvailableCores;
	private Reservations reservations;
	private ReservingSchedulerUtils reservingSchedulerUtils;
	private final SimbaConfiguration simbaConfiguration;

	@Inject
	public ReservingScheduler(AbstractWaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher, SimbaConfiguration simbaConfiguration)
	{
		this.cluster = cluster;
		this.waitingQueue = waitingQueue;
		this.grader = grader;
		this.dispatcher = dispatcher;
		this.simbaConfiguration = simbaConfiguration;
		reservationsSupplier = new ReservationsHolderSupplier(this.simbaConfiguration.reservationsLimit());
		DUMMY_JOB = Job.builder(1).cores(this.simbaConfiguration.jobCoresRatio() * 0.5).memory(1).build();
		// log.setLevel(Level.DEBUG);
	}

	@Override
	public int schedule(long time)
	{
		init();
		long started = System.currentTimeMillis();
		int processedJobsCount = 0;
		int scheduledJobs = 0;
		int skippedJobs = 0;
		Iterator<Job> iterator = waitingQueue.iterator();
		int startingHostsCount = currentCycleHosts.size();
		int startingJobsCount = waitingQueue.size();
		while (iterator.hasNext() && processedJobsCount < simbaConfiguration.jobsCheckedBySchduler() && !currentCycleHosts.isEmpty())
		{
			processedJobsCount++;
			Job job = iterator.next();
			Host host = getBestHost(job, shouldReserve(processedJobsCount));
			if (reservingSchedulerUtils.isAvailable(host, job))
			{
				dispatcher.dispatch(job, host, time);
				iterator.remove();
				scheduledJobs++;
			}
			else
			{
				if (shouldReserve(processedJobsCount))
				{
					reserve(host, job);
				}
			}
			updateCurrentCycleHosts(host);
			if (DUMMY_HOST.equals(host))
			{
				skippedJobs++;
			}
		}
		if (time % 10800 == 0)
		{
			logScheduler(time, scheduledJobs, processedJobsCount, startingHostsCount, skippedJobs, started, startingJobsCount);
		}
		return scheduledJobs;
	}

	private void logScheduler(long time, int scheduledJobs, int processedJobsCount, int startingHostsCount, int skippedJobs, long started, int startingJobsCount)
	{
		log.info("=============================================");
		log.info("schedule took " + (System.currentTimeMillis() - started));
		log.info("schedule - time " + time + " scheduled jobs " + scheduledJobs + " processed jobs " + processedJobsCount + " skippedJobs " + skippedJobs);
		log.info("schedule - avail-hosts start " + startingHostsCount + " avail-host end " + currentCycleHosts.size() + " wait-jobs start " + startingJobsCount
				+ " wait-jobs end " + waitingQueue.size());
		log.info("schedule -  first job " + waitingQueue.peek());
		if (!currentCycleHosts.isEmpty())
		{
			Host host = currentCycleHosts.get(0);
			log.info("schedule -  first host availableCores " + host.availableCores() + " availableMemory " + host.availableMemory());
		}
	}

	private boolean shouldReserve(int reservingJobsCount)
	{
		return reservingJobsCount <= simbaConfiguration.reservationsLimit();
	}

	private void updateCurrentCycleHosts(Host host)
	{
		if (isFull(host))
		{
			currentCycleHosts.remove(host);
		}
	}

	private List<Host> removeHostsThatAreFull(List<Host> hosts)
	{
		List<Host> $ = newArrayList();
		maxAvailableMemory = 0;
		maxAvailableCores = 0;
		for (Host host : hosts)
		{
			if (!isFull(host))
			{
				$.add(host);
				maxAvailableMemory = reservingSchedulerUtils.updateMaxAvailableMemory(host, maxAvailableMemory);
				maxAvailableCores = reservingSchedulerUtils.updateMaxAvailableCores(host, maxAvailableCores);
			}
		}
		return $;
	}

	private boolean isFull(Host host)
	{
		return !reservingSchedulerUtils.isAvailable(host, DUMMY_JOB);
	}

	private void init()
	{
		reservations = reservationsSupplier.get();
		reservingSchedulerUtils = new ReservingSchedulerUtils(reservations);
		currentCycleHosts = removeHostsThatAreFull(cluster.hosts());
	}

	private void reserve(Host host, Job job)
	{
		Reservation r = createExistingReservation(host, job);
		String id = host.id();
		asserter().throwsError().assertNotNull(id);
		reservations.put(id, r);
	}

	private Reservation createExistingReservation(Host host, Job job)
	{
		return Reservation.create(job, getReservation(host));
	}

	private Reservation getReservation(Host host)
	{
		return reservations.get(host.id());
	}

	private Host getBestHost(Job job, boolean shouldReserve)
	{
		if (!shouldReserve && (job.memory() > maxAvailableMemory || job.cores() > maxAvailableCores))
		{
			return DUMMY_HOST;
		}
		HostPicker hostPicker = new HostPicker(reservingSchedulerUtils, currentCycleHosts, grader);
		Host bestHost = hostPicker.getBestHost(job);
		maxAvailableMemory = hostPicker.maxAvailableMemory();
		return bestHost;
	}
}
