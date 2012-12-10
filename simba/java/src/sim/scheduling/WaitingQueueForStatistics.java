package sim.scheduling;

import java.util.Iterator;

import sim.model.Job;

public interface WaitingQueueForStatistics
{

	Iterator<Job> iterator();

	int collectRemove();

	int collectAdd();

	int size();

}
