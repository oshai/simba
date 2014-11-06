package sim.scheduling.reserving;

import java.util.List;

import sim.model.Host;
import sim.model.Job;
import sim.model.ReservingHost;
import sim.scheduling.graders.Grader;

public class HostPicker
{
	private double maxAvailableMemory;
	private final ReservingSchedulerUtils reservingSchedulerUtils;
	private final List<Host> currentCycleHosts;
	private final Grader grader;
	private final ReservingHostCollection reservingHostCollector;

	public HostPicker(ReservingSchedulerUtils reservingSchedulerUtils, List<Host> currentCycleHosts, Grader grader, ReservingHostCollection reservingHostCollector)
	{
		super();
		this.reservingSchedulerUtils = reservingSchedulerUtils;
		this.currentCycleHosts = currentCycleHosts;
		this.grader = grader;
		this.reservingHostCollector = reservingHostCollector;
	}

	public Host getBestHost(Job job)
	{
		Host selectedHost = ReservingScheduler.DUMMY_HOST;
		boolean isAvailable = false;

		for (Host host : currentCycleHosts)
		{
			maxAvailableMemory = reservingSchedulerUtils.updateMaxAvailableMemory(host, maxAvailableMemory);
			if (!host.hasPotentialResourceFor(job))
			{
				continue;
			}
			ReservingHost reservingHost = reservingHostCollector.getReservingHost(host, reservingSchedulerUtils);
			double grade = grader.getGrade(reservingHost, job);
			if (reservingSchedulerUtils.isAvailable(host, job))
			{
				ReservingHost reservingSelectedHost = reservingHostCollector.getReservingHost(selectedHost, reservingSchedulerUtils);
				if (!isAvailable || grade > grader.getGrade(reservingSelectedHost, job))
				{
					selectedHost = host;
					isAvailable = true;
				}
				// else grade lower
			}
			else if (!isAvailable) // and current host also not available
			{
				// optionally - change to grader 2
				// select host with more available memory
				double hostAvailable = reservingSchedulerUtils.availableMemory(host);
				double selectedHostAvailable = reservingSchedulerUtils.availableMemory(selectedHost);
				if (hostAvailable > selectedHostAvailable)
				{
					selectedHost = host;
				}
			}
			// else current host not available for job, but selected host is
			// available for job
			// so current host is not relevant
		}
		return selectedHost;
	}

	public double maxAvailableMemory()
	{
		return maxAvailableMemory;
	}

}