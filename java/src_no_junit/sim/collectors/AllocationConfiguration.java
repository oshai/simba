package sim.collectors;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

public class AllocationConfiguration
{
	private static final int PRIORITY_FACTOR = 1000000;
	private Map<String, QslotConfiguration> conf = newHashMap();

	public AllocationConfiguration()
	{
		super();
		createBaseQslots();
		createFastQslots();
		createSoftQslots();
	}

	public Map<String, QslotConfiguration> getAll()
	{
		return conf;
	}

	private void createBaseQslots()
	{
	}

	private void createSoftQslots()
	{
	}

	private void createFastQslots()
	{
	}
}
