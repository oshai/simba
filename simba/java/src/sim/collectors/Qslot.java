package sim.collectors;

public class Qslot
{

	private final QslotConfiguration conf;
	private double runningCost;

	public Qslot(QslotConfiguration conf)
	{
		super();
		this.conf = conf;
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
		return conf.maxCost();
	}

	public String name()
	{
		return conf.name();
	}

}
