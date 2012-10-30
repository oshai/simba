package sim.model;

import static com.google.common.collect.Lists.*;

import java.util.List;

public class Cluster implements HostsHolder
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
	
	@Override
	public void beforeUpdate(Host host)
	{
		// noting to do
	}
	
	@Override
	public void afterUpdate(Host host)
	{
		// noting to do
	}
	
}
