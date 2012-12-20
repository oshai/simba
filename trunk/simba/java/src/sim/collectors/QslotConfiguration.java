package sim.collectors;

public class QslotConfiguration
{

	private final String name;
	private final double maxCost;

	public QslotConfiguration(String name, double maxCost)
	{
		this.name = name;
		this.maxCost = maxCost;
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
