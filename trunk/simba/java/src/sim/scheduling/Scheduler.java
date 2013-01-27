package sim.scheduling;

import sim.model.Host;

public interface Scheduler
{
	public static final Host DUMMY_HOST = Host.builder().id("dummy").cores(0).memory(0).build();

	public int schedule(long time);

}
