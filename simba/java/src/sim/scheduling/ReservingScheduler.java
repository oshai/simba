package sim.scheduling;

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
import sim.scheduling.graders.Grader;
import utils.GlobalUtils;

public class ReservingScheduler implements Scheduler
{
	private static final Logger log = Logger.getLogger(ReservingScheduler.class);
	private final WaitingQueue waitingQueue;
	private final Cluster cluster;
	private final Grader grader;
	private final JobDispatcher dispatcher;
	private Map<String, Reservation> reservations;
	private List<Host> currentCycleHosts;
	private static final Job DUMMY_JOB = Job.create(1).cores(1).memory(1).build();
	private static final int RESERVATIONS = 1;
	public static final int JOBS_CHECKED_BY_SCHEDULER = 1000;

	public ReservingScheduler(WaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher)
	{
		this.cluster = cluster;
		this.waitingQueue = waitingQueue;
		this.grader = grader;
		this.dispatcher = dispatcher;
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
			Host host = getBestHost(job);
			if (isAvailable(host, job))
			{
				dispatcher.dispatch(job, host, time);
				iterator.remove();
				scheduledJobs++;
			}
			else
			{
				if (reservingJobsCount < RESERVATIONS)
				{
					reserve(host, job);
					reservingJobsCount++;
				}
			}
			updateCurrentCycleHosts(host);
		}
		if (log.isDebugEnabled() && time % 3600 == 0)
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
		for (Host host : hosts)
		{
			if (!isFull(host))
			{
				$.add(host);
			}
		}
		return $;
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

	protected Reservation getReservation(Host host)
	{
		return reservations.containsKey(host.id()) ? reservations.get(host.id()) : Reservation.NULL_OBJECT;
	}

	public static class Reservation
	{
		public static Reservation NULL_OBJECT = new Reservation(0, 0);
		private double cores, memory;

		public Reservation(double cores, double memory)
		{
			this.cores = cores;
			this.memory = memory;
		}

		public static Reservation create(Job job, Reservation existReservation)
		{
			return new Reservation(existReservation.cores() + job.cores(), existReservation.memory() + job.memory());
		}

		public double cores()
		{
			return cores;
		}

		public double memory()
		{
			return memory;
		}
	}

	private boolean isAvailable(Host host, Job job)
	{
		Reservation r = createExistingReservation(host, job);
		return greaterOrEquals(host.availableCores(), r.cores()) && greaterOrEquals(host.availableMemory(), r.memory());
	}

	private Host getBestHost(Job job)
	{
		Host selectedHost = null;
		boolean isAvailable = false;
		for (Host host : currentCycleHosts)
		{
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
				double hostAvailable = host.availableMemory() - getReservation(host).memory();
				double selectedHostAvailable = selectedHost.availableMemory() - getReservation(selectedHost).memory();
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
