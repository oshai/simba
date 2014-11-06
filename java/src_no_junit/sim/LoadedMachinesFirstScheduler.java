package sim;

import sim.model.Cluster;
import sim.scheduling.JobDispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.graders.Grader;
import sim.scheduling.waiting_queue.WaitingQueue;

public class LoadedMachinesFirstScheduler implements Scheduler
{

	public LoadedMachinesFirstScheduler(WaitingQueue waitingQueue, Cluster cluster, Grader grader, JobDispatcher dispatcher)
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public int schedule(long time)
	{
		throw new RuntimeException();
	}
}
