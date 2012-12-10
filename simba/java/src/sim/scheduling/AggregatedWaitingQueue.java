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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int collectAdd()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
