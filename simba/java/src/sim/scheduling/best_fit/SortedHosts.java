package sim.scheduling.best_fit;

import java.util.TreeSet;

import sim.model.Cluster;
import sim.model.Host;
import sim.model.HostsHolder;

public class SortedHosts implements HostsHolder
{
	private final TreeSet<Host> hosts;
	
	public SortedHosts(Cluster cluster)
	{
		super();
		this.hosts = init(cluster);
	}
	
	private TreeSet<Host> init(Cluster cluster)
	{
		TreeSet<Host> treeSet = new TreeSet<Host>(new HostsComperator());
		for (Host host : cluster.hosts())
		{
			treeSet.add(host);
		}
		return treeSet;
	}
	
	public Iterable<Host> hosts()
	{
		return hosts;
	}
	
	public boolean add(Host host)
	{
		return hosts.add(host);
	}
	
	@Override
	public void beforeUpdate(Host host)
	{
		hosts.remove(host);
	}
	
	@Override
	public void afterUpdate(Host host)
	{
		add(host);
	}
}
