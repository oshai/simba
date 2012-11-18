package sim.scheduling;

import static utils.GlobalUtils.*;
import static utils.assertions.Asserter.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sim.Looper;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.graders.Grader;
import utils.GlobalUtils;

public class ReservingScheduler implements Scheduler
{
	private final WaitingQueue waitingQueue;
	private final Cluster cluster;
	private final Grader grader;
	private final Dispatcher dispatcher;
	private Map<String, Reservation> reservations;

	public ReservingScheduler(WaitingQueue waitingQueue, Cluster cluster, Grader grader, Dispatcher dispatcher)
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
		Iterator<Job> iterator = waitingQueue.iterator();
		while (iterator.hasNext() && processedJobsCount < Looper.JOBS_CHECKED_BY_SCHEDULER)
		{
			processedJobsCount++;
			Job job = iterator.next();
			Host host = getBestHost(job);
			if (isAvailable(host, job))
			{
				dispatcher.dipatch(job, host, time);
				iterator.remove();
			}
			else
			{
				if (reservingJobsCount < 1)
				{
					reserve(host, job);
					reservingJobsCount++;
				}
			}
		}
	}

	private void init()
	{
		reservations = new HashMap<String, Reservation>();
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
		for (Host host : cluster.hosts())
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
