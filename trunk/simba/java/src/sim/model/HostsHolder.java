package sim.model;

public interface HostsHolder
{
	public boolean add(Host host);
	
	public Iterable<Host> hosts();
	
	public void beforeUpdate(Host host);
	
	public void afterUpdate(Host host);
	
}
