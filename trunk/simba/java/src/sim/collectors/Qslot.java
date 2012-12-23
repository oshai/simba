package sim.collectors;

public class Qslot
{

	private final QslotConfiguration configuration;
	private double runningCost;
	private double gettingNow;
	private double absoluteShouldGet;
	private boolean hasWaitingJobs;
	private boolean hasRunningJobs;
	private double relativeRunningShouldGet;
	private double relativeWaitingShouldGet;

	public Qslot(QslotConfiguration conf)
	{
		super();
		this.configuration = conf;
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
		return absoluteShouldGet;
	}

	public QslotConfiguration configuration()
	{
		return configuration;
	}

	public void absoluteShouldGet(double d)
	{
		absoluteShouldGet = d;
	}

	public double absoluteShouldGetDelta()
	{
		return absoluteShouldGet - gettingNow;
	}

	public double absoluteShouldGetError()
	{
		return hasWaitingJobs ? Math.max(0.0, absoluteShouldGetDelta()) : 0.0;
	}

	public void hasWaitingJobs(boolean b)
	{
		hasWaitingJobs = b;
	}

	public double relativeRunningShouldGet()
	{
		return relativeRunningShouldGet;
	}

	public double relativeRunningShouldGetDelta()
	{
		return relativeRunningShouldGet - gettingNow;
	}

	public double relativeRunningShouldGetError()
	{
		return hasWaitingJobs ? Math.max(0.0, relativeRunningShouldGetDelta()) : 0.0;
	}

	public void hasRunningJobs(boolean b)
	{
		hasRunningJobs = b;
	}

	public boolean hasRunningJobs()
	{
		return hasRunningJobs;
	}

	public void relativeShouldGet(double d)
	{
		relativeRunningShouldGet = d;
	}

	public double relativeWaitingShouldGet()
	{
		return relativeWaitingShouldGet;
	}

	public double relativeWaitingShouldGetError()
	{
		return hasWaitingJobs ? Math.max(0.0, relativeWaitingShouldGetDelta()) : 0.0;
	}

	private double relativeWaitingShouldGetDelta()
	{
		return relativeWaitingShouldGet - gettingNow;
	}

	public boolean hasWaitingJobs()
	{
		return hasWaitingJobs;
	}

	public void relativeWaitingShouldGet(double d)
	{
		relativeWaitingShouldGet = d;
	}

}
