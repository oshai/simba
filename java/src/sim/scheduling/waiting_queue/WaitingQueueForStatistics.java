package sim.scheduling.waiting_queue;

import sim.model.*;

public interface WaitingQueueForStatistics extends Iterable<Job>
{

	int collectRemove();

	int collectAdd();

	int size();

}
