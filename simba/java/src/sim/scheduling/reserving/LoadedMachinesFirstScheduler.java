package sim.scheduling.reserving;

import sim.model.Cluster;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.JobDispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.graders.Grader;

public class LoadedMachinesFirstScheduler implements Scheduler
{

	public LoadedMachinesFirstScheduler(AbstractWaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher)
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public int schedule(long time)
	{
		throw new RuntimeException();
	}
}
