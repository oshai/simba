package sim.distributed;

import static com.google.common.collect.Lists.*;

import java.util.List;

import sim.model.Job;

public class ByStrategyHostSelector implements HostSelector
{
	private final List<HostScheduler> hostSchedulers;
	private List<HostScheduler> leftHostSchedulers;
	private Job lastJob;
	private ListSelector listSelector;

	public ByStrategyHostSelector(List<HostScheduler> hostSchedulers, ListSelector listSelector)
	{
		this.hostSchedulers = hostSchedulers;
		leftHostSchedulers = newArrayList(hostSchedulers);
		this.listSelector = listSelector;
	}

	@Override
	public HostScheduler select(Job job)
	{
		if (!job.equals(lastJob))
		{
			lastJob = job;
			leftHostSchedulers = newArrayList(hostSchedulers);
		}
		return listSelector.selectFromList(leftHostSchedulers);
	}

}
