package sim.scheduling;

import java.util.ArrayList;
import java.util.Iterator;

import sim.model.Job;

import com.google.common.collect.Lists;

public class AggregatedWaitingQueue implements IWaitingQueue
{

	private final ArrayList<HostScheduler> hostSchedulers;

	public AggregatedWaitingQueue(ArrayList<HostScheduler> hostSchedulers)
	{
		this.hostSchedulers = hostSchedulers;
	}

	@Override
	public Iterator<Job> iterator()
	{
		return Lists.<Job> newArrayList().iterator();
	}

	@Override
	public int collectRemove()
	{
		int $ = 0;
		for (HostScheduler h : hostSchedulers)
		{
			$ += h.collectRemove();
		}
		return $;
	}

	@Override
	public int collectAdd()
	{
		int $ = 0;
		for (HostScheduler h : hostSchedulers)
		{
			$ += h.collectAdd();
		}
		return $;
	}

	@Override
	public int size()
	{
		int $ = 0;
		for (HostScheduler h : hostSchedulers)
		{
			$ += h.waitingJobs();
		}
		return $;
	}

}
