package sim.scheduling;

import sim.model.*;

public interface WaitingQueueForStatistics extends Iterable<Job>
{

	int collectRemove();

	int collectAdd();

	int size();

}
