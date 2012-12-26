package sim.model;

import static com.google.common.collect.Lists.*;

import java.util.List;

public class Cluster
{
	private List<Host> hosts = newArrayList();

	public Cluster add(Host host)
	{
		hosts.add(host);
		return this;
	}

	public List<Host> hosts()
	{
		return hosts;
	}
}
