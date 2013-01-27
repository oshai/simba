package sim.scheduling.reserving;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static utils.assertions.Asserter.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import sim.SimbaConfiguration;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.JobDispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.graders.Grader;
import sim.scheduling.waiting_queue.WaitingQueue;

public class ReservingScheduler implements Scheduler
{
	private static final Logger log = Logger.getLogger(ReservingScheduler.class);
	private final Job DUMMY_JOB;
	private final WaitingQueue waitingQueue;
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
	private long started;
	private int processedJobsCount;
	private int scheduledJobs;
	private int skippedJobs;
	private int startingHostsCount;
	private int startingJobsCount;

	@Inject
	public ReservingScheduler(WaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher, SimbaConfiguration simbaConfiguration)
	{
		this.cluster = cluster;
		this.waitingQueue = waitingQueue;
		this.grader = grader;
		this.dispatcher = dispatcher;
		this.simbaConfiguration = simbaConfiguration;
		reservationsSupplier = new ReservationsHolderSupplier(this.simbaConfiguration.reservationsLimit());
		DUMMY_JOB = Job.builder(1).cores(1).memory(1).build();
		// log.setLevel(Level.DEBUG);
	}

	@Override
	public final int schedule(long time)
	{
		Map<Job, Host> dispatchedJobs = scheduleWithoutDispatch(time);
		return dispatchAndReport(dispatchedJobs, time);
	}

	private int dispatchAndReport(Map<Job, Host> dispatchedJobs, long time)
	{
		dispatch(dispatchedJobs, time);
		if (shouldReport(time))
		{
			logScheduler(time);
		}
		return scheduledJobs;
	}

	public Map<Job, Host> scheduleWithoutDispatch(long time)
	{
		init();
		Map<Job, Host> dispatchedJobs = selectJobsToDispatch(time);
		return dispatchedJobs;
	}

	private void dispatch(Map<Job, Host> dispatchedJobs, long time)
	{
		Iterator<Job> iterator = waitingQueue.iterator();
		int processedJobsCount2 = 0;
		while (iterator.hasNext() && processedJobsCount2 < simbaConfiguration.jobsCheckedBySchduler())
		{
			processedJobsCount2++;
			Job job = iterator.next();
			Host host = dispatchedJobs.get(job);
			if (null != host && !DUMMY_HOST.equals(host))
			{
				scheduledJobs++;
				dispatcher.dispatch(job, host, time);
				iterator.remove();
			}
		}
	}

	protected Map<Job, Host> selectJobsToDispatch(long time)
	{
		Map<Job, Host> dispatchedJobs = newHashMap();
		Iterator<Job> iterator = waitingQueue.iterator();
		int processedJobsCount2 = 0;
		while (iterator.hasNext() && processedJobsCount2 < simbaConfiguration.jobsCheckedBySchduler() && !currentCycleHosts.isEmpty())
		{
			processedJobsCount2++;
			Job job = iterator.next();
			Host host = getBestHost(job, shouldReserve(processedJobsCount2));
			if (DUMMY_HOST.equals(host))
			{
				skippedJobs++;
				continue;
			}
			boolean dispatched = false;
			if (reservingSchedulerUtils.isAvailable(host, job))
			{
				dispatchedJobs.put(job, host);
				dispatched = true;
			}
			if (shouldReserve(processedJobsCount2) || dispatched)
			{
				reserve(host, job);
			}
			updateCurrentCycleHosts(host);
		}
		processedJobsCount = processedJobsCount2;
		return dispatchedJobs;
	}

	protected final boolean shouldReport(long time)
	{
		return time % 10800 == 0;
	}

	private void logScheduler(long time)
	{
		log.info("=============================================");
		log.info("schedule took " + (System.currentTimeMillis() - started));
		log.info("schedule - time " + time + " scheduled jobs " + scheduledJobs + " processed jobs " + processedJobsCount + " skippedJobs " + skippedJobs);
		log.info("schedule - avail-hosts start " + startingHostsCount + " avail-host end " + currentCycleHosts.size() + " wait-jobs start " + startingJobsCount + " wait-jobs end " + waitingQueue.size());
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
		started = System.currentTimeMillis();
		reservations = reservationsSupplier.get();
		reservingSchedulerUtils = new ReservingSchedulerUtils(reservations);
		currentCycleHosts = removeHostsThatAreFull(cluster.hosts());
		processedJobsCount = 0;
		scheduledJobs = 0;
		skippedJobs = 0;
		startingHostsCount = currentCycleHosts.size();
		startingJobsCount = waitingQueue.size();
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

	public Grader grader()
	{
		return grader;
	}
}
