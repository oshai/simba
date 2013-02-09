package sim.configuration;

import java.util.List;

import javax.inject.Inject;

import sim.SimbaConfiguration;
import sim.model.Cluster;
import sim.scheduling.JobDispatcher;
import sim.scheduling.reserving.ReservingScheduler;
import sim.scheduling.waiting_queue.WaitingQueue;

import com.google.common.collect.Lists;

public class SimpleSchedulersProvider extends SchedulersProvider
{

	@Inject
	public SimpleSchedulersProvider(WaitingQueue waitingQueue, SimbaConfiguration simbaConfiguration, JobDispatcher dispatcher, Cluster cluster)
	{
		super(waitingQueue, simbaConfiguration, dispatcher, cluster);
	}

	@Override
	public List<ReservingScheduler> get()
	{
		List<ReservingScheduler> l = Lists.newArrayList(createScheduler("BEST-FIT"), createScheduler("WF"), createScheduler("FF"), createScheduler("BFC"), createScheduler("WFC"));
		return l;
	}

}