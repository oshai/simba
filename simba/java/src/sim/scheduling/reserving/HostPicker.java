package sim.scheduling.reserving;

import java.util.List;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.graders.Grader;

public class HostPicker
{
	private double maxAvailableMemory;
	private final ReservingSchedulerUtils reservingSchedulerUtils;
	private final List<Host> currentCycleHosts;
	private final Grader grader;

	public HostPicker(ReservingSchedulerUtils reservingSchedulerUtils, List<Host> currentCycleHosts, Grader grader)
	{
		super();
		this.reservingSchedulerUtils = reservingSchedulerUtils;
		this.currentCycleHosts = currentCycleHosts;
		this.grader = grader;
	}

	public Host getBestHost(Job job)
	{
		Host selectedHost = null;
		boolean isAvailable = false;

		for (Host host : currentCycleHosts)
		{
			maxAvailableMemory = reservingSchedulerUtils.updateMaxAvailableMemory(host, maxAvailableMemory);
			double grade = grader.getGrade(host, job);
			if (null == selectedHost)
			{
				selectedHost = host;
				isAvailable = reservingSchedulerUtils.isAvailable(host, job);
			}
			else if (reservingSchedulerUtils.isAvailable(host, job))
			{
				if (!isAvailable || grade > grader.getGrade(selectedHost, job))
				{
					selectedHost = host;
					isAvailable = true;
				}
				// else grade lower
			}
			else if (!isAvailable)
			// and current host also not available
			{
				// TODO check for job that cannot run on the machine
				// TODO change to grader 2
				// select host with more available memory
				double hostAvailable = reservingSchedulerUtils.availableMemory(host);
				double selectedHostAvailable = reservingSchedulerUtils.availableMemory(selectedHost);
				if (hostAvailable > selectedHostAvailable)
				{
					selectedHost = host;
				}
				// TODO add test for this case
			}
			// else selectedHost is available and this host not
			// TODO check this ???
		}
		return selectedHost;
	}

	public double maxAvailableMemory()
	{
		return maxAvailableMemory;
	}

}