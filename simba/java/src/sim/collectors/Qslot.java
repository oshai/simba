package sim.collectors;

import java.util.EnumMap;
import java.util.Map;

public class Qslot
{
	public static enum SHOULD_GET
	{
		ABSOLUTE, RELATIVE, RELATIVE_RUNNING, RELATIVE_WAITING,
	}

	private final QslotConfiguration configuration;
	private double runningCost;
	private double gettingNow;
	private boolean hasWaitingJobs;
	private boolean hasRunningJobs;
	private Map<SHOULD_GET, Double> shouldGet = new EnumMap<Qslot.SHOULD_GET, Double>(SHOULD_GET.class);

	public Qslot(QslotConfiguration conf)
	{
		super();
		this.configuration = conf;
		for (SHOULD_GET t : SHOULD_GET.values())
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
		return shouldGet.get(SHOULD_GET.ABSOLUTE);
	}

	public QslotConfiguration configuration()
	{
		return configuration;
	}

	public void absoluteShouldGet(double d)
	{
		shouldGet.put(SHOULD_GET.ABSOLUTE, d);
	}

	public double absoluteShouldGetDelta()
	{
		return getShouldGetDelta(SHOULD_GET.ABSOLUTE);
	}

	public double absoluteShouldGetError()
	{
		return getShouldGetError(SHOULD_GET.ABSOLUTE);
	}

	public void hasWaitingJobs(boolean b)
	{
		hasWaitingJobs = b;
	}

	public double relativeRunningShouldGet()
	{
		return shouldGet.get(SHOULD_GET.RELATIVE_RUNNING);
	}

	public double relativeRunningShouldGetDelta()
	{
		return getShouldGetDelta(SHOULD_GET.RELATIVE_RUNNING);
	}

	public double relativeRunningShouldGetError()
	{
		return getShouldGetError(SHOULD_GET.RELATIVE_RUNNING);
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
		shouldGet.put(SHOULD_GET.RELATIVE_RUNNING, d);
	}

	public double relativeWaitingShouldGet()
	{
		return shouldGet.get(SHOULD_GET.RELATIVE_WAITING);
	}

	public double relativeWaitingShouldGetError()
	{
		return getShouldGetError(SHOULD_GET.RELATIVE_WAITING);
	}

	public boolean hasWaitingJobs()
	{
		return hasWaitingJobs;
	}

	public void relativeWaitingShouldGet(double d)
	{
		shouldGet.put(SHOULD_GET.RELATIVE_WAITING, d);
	}

	public double relativeShouldGetError()
	{
		return getShouldGetError(SHOULD_GET.RELATIVE);
	}

	private double getShouldGetError(SHOULD_GET type)
	{
		return hasWaitingJobs ? Math.max(0.0, getShouldGetDelta(type)) : 0.0;
	}

	public void relativeShouldGet(double d)
	{
		this.shouldGet.put(SHOULD_GET.RELATIVE, d);

	}

	public double relativeShouldGetDelta()
	{
		return getShouldGetDelta(SHOULD_GET.RELATIVE);
	}

	private double getShouldGetDelta(SHOULD_GET type)
	{
		return shouldGet.get(type) - gettingNow;
	}

	public double allocation()
	{
		return configuration().allocation();
	}

}
