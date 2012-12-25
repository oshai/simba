package sim.collectors;

import java.util.EnumMap;
import java.util.Map;

import sim.model.Job;

public class Qslot
{
	private final QslotConfiguration configuration;
	private double runningCost;
	private double gettingNow;
	private boolean hasWaitingJobs;
	private boolean hasRunningJobs;
	private Map<ShouldGet, Double> shouldGet = new EnumMap<ShouldGet, Double>(ShouldGet.class);
	private Job longestWaitingJob;

	public Qslot(QslotConfiguration conf)
	{
		super();
		this.configuration = conf;
		for (ShouldGet t : ShouldGet.values())
		{
			shouldGet.put(t, 0.0);
		}
	}

	public void addCost(double cost)
	{
		runningCost += cost;
	}

	public double cost()
	{
		return runningCost;
	}

	public double maxCost()
	{
		return configuration.maxCost();
	}

	public String name()
	{
		return configuration.name();
	}

	public double costError()
	{
		return (maxCost() < runningCost) ? runningCost - maxCost() : 0;
	}

	public double gettingNow()
	{
		return gettingNow;
	}

	public void gettingNow(double d)
	{
		gettingNow = d;
	}

	public double absoluteShouldGet()
	{
		return shouldGet.get(ShouldGet.ABSOLUTE);
	}

	public QslotConfiguration configuration()
	{
		return configuration;
	}

	public void absoluteShouldGet(double d)
	{
		shouldGet.put(ShouldGet.ABSOLUTE, d);
	}

	public double absoluteShouldGetDelta()
	{
		return getShouldGetDelta(ShouldGet.ABSOLUTE);
	}

	public double absoluteShouldGetError()
	{
		return getShouldGetError(ShouldGet.ABSOLUTE);
	}

	public void hasWaitingJobs(boolean b)
	{
		hasWaitingJobs = b;
	}

	public double relativeRunningShouldGet()
	{
		return shouldGet.get(ShouldGet.RELATIVE_RUNNING);
	}

	public double relativeRunningShouldGetDelta()
	{
		return getShouldGetDelta(ShouldGet.RELATIVE_RUNNING);
	}

	public double relativeRunningShouldGetError()
	{
		return getShouldGetError(ShouldGet.RELATIVE_RUNNING);
	}

	public void hasRunningJobs(boolean b)
	{
		hasRunningJobs = b;
	}

	public boolean hasRunningJobs()
	{
		return hasRunningJobs;
	}

	public void relativeRunningShouldGet(double d)
	{
		shouldGet.put(ShouldGet.RELATIVE_RUNNING, d);
	}

	public double relativeWaitingShouldGet()
	{
		return shouldGet.get(ShouldGet.RELATIVE_WAITING);
	}

	public double relativeWaitingShouldGetError()
	{
		return getShouldGetError(ShouldGet.RELATIVE_WAITING);
	}

	public boolean hasWaitingJobs()
	{
		return hasWaitingJobs;
	}

	public void relativeWaitingShouldGet(double d)
	{
		shouldGet.put(ShouldGet.RELATIVE_WAITING, d);
	}

	public double relativeShouldGetError()
	{
		return getShouldGetError(ShouldGet.RELATIVE);
	}

	private double getShouldGetError(ShouldGet type)
	{
		return hasWaitingJobs ? Math.max(0.0, getShouldGetDelta(type)) : 0.0;
	}

	public void relativeShouldGet(double d)
	{
		this.shouldGet.put(ShouldGet.RELATIVE, d);

	}

	public double relativeShouldGetDelta()
	{
		return getShouldGetDelta(ShouldGet.RELATIVE);
	}

	private double getShouldGetDelta(ShouldGet type)
	{
		return shouldGet.get(type) - gettingNow;
	}

	public double allocation()
	{
		return configuration().allocation();
	}

	public Job longestWaitingJob()
	{
		return longestWaitingJob;
	}

	public void updateLongestWaitingJob(Job j)
	{
		longestWaitingJob = shouldUpdate(j) ? j : longestWaitingJob;
	}

	private boolean shouldUpdate(Job j)
	{
		return longestWaitingJob == null || isOlderFromLongestWaitingJob(j);
	}

	private boolean isOlderFromLongestWaitingJob(Job j)
	{
		return longestWaitingJob.submitTime() > j.submitTime();
	}

	public double jobError(long time)
	{
		if (longestWaitingJob == null)
			return 0.0;
		return (relativeShouldGetError() * (time - longestWaitingJob.submitTime())) / longestWaitingJob.cost();
	}
}
