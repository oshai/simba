package sim.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class Cluster
{
	private List<Host> hosts = newArrayList();

	public boolean add(Host host)
	{
		return hosts.add(host);
	}

	public List<Host> hosts()
	{
		return hosts;
	}

}
