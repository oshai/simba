package sim.scheduling.reserving;

import static com.google.common.collect.Maps.*;

import java.util.Map;

import sim.model.Host;
import sim.model.ReservingHost;

public class ReservingHostCollection
{
	private Map<Host, ReservingHost> map = newHashMap();

	public ReservingHost getReservingHost(Host host, ReservingSchedulerUtils reservingSchedulerUtils)
	{
		if (!map.containsKey(host))
		{
			map.put(host, new ReservingHost(host, reservingSchedulerUtils));
		}
		return map.get(host);
	}

}
