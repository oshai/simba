package sim.scheduling.reserving;

import com.google.common.base.Supplier;

public class ReservationsHolderSupplier implements Supplier<Reservations>
{

	private Supplier<Reservations> reservations;

	public ReservationsHolderSupplier(int maxReservations)
	{
		super();
		// if (maxReservations <= 1)
		// {
		// reservations = new Supplier<Reservations>()
		// {
		//
		// @Override
		// public Reservations get()
		// {
		// return new SingleReservationHolder();
		// }
		// };
		// }
		// else
		// {
		reservations = new Supplier<Reservations>()
		{

			@Override
			public Reservations get()
			{
				return new ReservationsHolder();
			}
		};
		// }
	}

	@Override
	public Reservations get()
	{
		return reservations.get();
	}

}
