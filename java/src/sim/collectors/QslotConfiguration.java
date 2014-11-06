package sim.collectors;

public class QslotConfiguration
{

	private final String name;
	private final double maxCost;
	private final double allocation;

	public QslotConfiguration(String name, double maxCost, double allocation)
	{
		this.name = name;
		this.maxCost = maxCost;
		this.allocation = allocation;
	}

	public double allocation()
	{
		return allocation;
	}

	public double maxCost()
	{
		return maxCost;
	}

	public String name()
	{
		return name;
	}

}
