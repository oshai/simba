package sim.scheduling.reserving;

import static utils.GlobalUtils.*;
import sim.model.Host;
import sim.model.Job;

public class ReservingSchedulerUtils
{
	private final Reservations reservations;

	public ReservingSchedulerUtils(Reservations reservations)
	{
		this.reservations = reservations;
	}

	public Reservation getReservation(Host host)
	{
		return reservations.get(host.id());
	}

	public double updateMaxAvailableMemory(Host host, double maxAvailableMemory)
	{
		if (availableMemory(host) > maxAvailableMemory)
		{
			return host.availableMemory();
		}
		return maxAvailableMemory;
	}

	public double availableMemory(Host host)
	{
		return host.availableMemory() - getReservation(host).memory();
	}

	public boolean isAvailable(Host host, Job job)
	{
		Reservation r = getReservation(host);
		return greaterOrEquals(host.availableCores(), r.cores() + job.cores()) && greaterOrEquals(host.availableMemory(), r.memory() + job.memory());
	}

	public double availableCores(Host host)
	{
		return host.availableCores() - getReservation(host).cores();
	}

	public double updateMaxAvailableCores(Host host, double maxAvailableCores)
	{
		if (availableCores(host) > maxAvailableCores)
		{
			return host.availableCores();
		}
		return maxAvailableCores;
	}
}