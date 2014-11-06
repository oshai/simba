package sim.scheduling.reserving;

import sim.model.Job;

public class Reservation
{
	public static Reservation NULL_OBJECT = new Reservation(0, 0);
	private double cores, memory;

	public Reservation(double cores, double memory)
	{
		this.cores = cores;
		this.memory = memory;
	}

	public static Reservation create(Job job, Reservation existReservation)
	{
		return new Reservation(existReservation.cores() + job.cores(), existReservation.memory() + job.memory());
	}

	public double cores()
	{
		return cores;
	}

	public double memory()
	{
		return memory;
	}
}