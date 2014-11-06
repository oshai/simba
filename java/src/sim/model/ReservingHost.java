package sim.model;

import sim.scheduling.reserving.ReservingSchedulerUtils;

public class ReservingHost implements GradeableHost
{

	private final Host host;
	private final ReservingSchedulerUtils reservingSchedulerUtils;

	public ReservingHost(Host host, ReservingSchedulerUtils reservingSchedulerUtils)
	{
		this.host = host;
		this.reservingSchedulerUtils = reservingSchedulerUtils;
	}

	@Override
	public double availableCores()
	{
		return reservingSchedulerUtils.availableCores(host);
	}

	@Override
	public double availableMemory()
	{
		return reservingSchedulerUtils.availableMemory(host);
	}

	@Override
	public double memory()
	{
		return host.memory();
	}

	@Override
	public double cores()
	{
		return host.cores();
	}

	@Override
	public double usedMemory()
	{
		return memory() - availableMemory();
	}

	@Override
	public double usedCores()
	{
		return cores() - availableCores();
	}

}
