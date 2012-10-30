package sim.scheduling.best_fit;

import java.util.Comparator;

import sim.model.Host;

import com.intel.swiss.sws.mechanism.utils.GlobalUtils;

public class HostsComperator implements Comparator<Host>
{
	@Override
	public int compare(Host o1, Host o2)
	{
		if (GlobalUtils.equals(o1.availableMemory(), o2.availableMemory()))
		{
			return o1.hashCode() - o2.hashCode();
		}
		return o1.availableMemory() - o2.availableMemory() > 0 ? 1 : -1;
	}
}