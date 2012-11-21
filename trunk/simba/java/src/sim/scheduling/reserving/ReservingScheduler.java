package sim.scheduling.reserving;

import static com.google.common.collect.Lists.*;
import static utils.GlobalUtils.*;
import static utils.assertions.Asserter.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.JobDispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.WaitingQueue;
import sim.scheduling.graders.Grader;
import utils.GlobalUtils;

public class ReservingScheduler implements Scheduler
{
	private static final Logger log = Logger.getLogger(ReservingScheduler.class);
	public static final int JOBS_CHECKED_BY_SCHEDULER = Integer.MAX_VALUE;
	public static final int RESERVATIONS = 1;
	private static final Job DUMMY_JOB = Job.create(1).cores(1).memory(1).build();
	private static final Host DUMMY_HOST = Host.create().id("dummy").cores(0).memory(0).build();
	private final WaitingQueue waitingQueue;
	private final Cluster cluster;
	private final Grader grader;
	private final JobDispatcher dispatcher;
	private Map<String, Reservation> reservations;
	private List<Host> currentCycleHosts;
	private double maxAvailableMemory;
	public final int reservationsLimit;

	public ReservingScheduler(WaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher)
	{
		this(waitingQueue, cluster, grader, dispatcher, RESERVATIONS);
	}

	public ReservingScheduler(WaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher, int reservationsLimit)
	{
		this.cluster = cluster;
		this.waitingQueue = waitingQueue;
		this.grader = grader;
		this.dispatcher = dispatcher;
		this.reservationsLimit = reservationsLimit;
	}

	@Override
	public void schedule(long time)
	{
		init();
		int reservingJobsCount = 0;
		int processedJobsCount = 0;
		int scheduledJobs = 0;
		Iterator<Job> iterator = waitingQueue.iterator();
		while (iterator.hasNext() && processedJobsCount < ReservingScheduler.JOBS_CHECKED_BY_SCHEDULER && !currentCycleHosts.isEmpty())
		{
			processedJobsCount++;
			Job job = iterator.next();
			Host host = getBestHost(job, shouldReserve(reservingJobsCount));
			if (isAvailable(host, job))
			{
				dispatcher.dispatch(job, host, time);
				iterator.remove();
				scheduledJobs++;
			}
			else
			{
				if (shouldReserve(reservingJobsCount))
				{
					reserve(host, job);
				}
			}
			updateCurrentCycleHosts(host);
			reservingJobsCount++;
		}
		if (log.isDebugEnabled() && time % 3600 == 0)
		{
			logScheduler(time, scheduledJobs);
		}
	}

	private void logScheduler(long time, int scheduledJobs)
	{
		log.info("schedule() - time " + time + " scheduled jobs " + scheduledJobs);
		log.info("schedule() - avail-hosts " + currentCycleHosts.size() + " wait-jobs " + waitingQueue.size());
		log.info("schedule() -  first job " + waitingQueue.peek());
		if (!currentCycleHosts.isEmpty())
		{
			Host host = currentCycleHosts.get(0);
			log.info("schedule() -  first host availableCores " + host.availableCores() + " availableMemory " + host.availableMemory());
		}
	}

	private boolean shouldReserve(int reservingJobsCount)
	{
		return reservingJobsCount < reservationsLimit;
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
		for (Host host : hosts)
		{
			if (!isFull(host))
			{
				$.add(host);
				updateMaxAvailableMemory(host);
			}
		}
		return $;
	}

	private void updateMaxAvailableMemory(Host host)
	{
		if (greater(availableMemory(host), maxAvailableMemory))
		{
			maxAvailableMemory = host.availableMemory();
		}
	}

	private double availableMemory(Host host)
	{
		return host.availableMemory() - getReservation(host).memory();
	}

	private boolean isFull(Host host)
	{
		return !isAvailable(host, DUMMY_JOB);
	}

	private void init()
	{
		reservations = new HashMap<String, Reservation>();
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
		return reservations.containsKey(host.id()) ? reservations.get(host.id()) : Reservation.NULL_OBJECT;
	}

	private boolean isAvailable(Host host, Job job)
	{
		Reservation r = createExistingReservation(host, job);
		return greaterOrEquals(host.availableCores(), r.cores()) && greaterOrEquals(host.availableMemory(), r.memory());
	}

	private Host getBestHost(Job job, boolean shouldReserve)
	{
		if (!shouldReserve && GlobalUtils.greater(job.memory(), maxAvailableMemory))
		{
			return DUMMY_HOST;
		}
		Host selectedHost = null;
		boolean isAvailable = false;
		maxAvailableMemory = 0;
		for (Host host : currentCycleHosts)
		{
			updateMaxAvailableMemory(host);
			double grade = grader.getGrade(host, job);
			if (null == selectedHost)
			{
				selectedHost = host;
				isAvailable = isAvailable(host, job);
			}
			else if (isAvailable(host, job))
			{
				if (!isAvailable || greater(grade, grader.getGrade(selectedHost, job)))
				{
					selectedHost = host;
					isAvailable = true;
				}
				// else grade lower
			}
			else if (!isAvailable) // and current host also not available
			{
				// select host with more available memory
				double hostAvailable = availableMemory(host);
				double selectedHostAvailable = availableMemory(selectedHost);
				if (GlobalUtils.greater(hostAvailable, selectedHostAvailable))
				{
					selectedHost = host;
				}
			}
			// else selectedHost is available and this host not
		}
		return selectedHost;
	}
}
