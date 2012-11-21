package sim.scheduling.reserving;

public class SingleReservationHolder implements Reservations
{
	private static final String NOT_EXIST_HOST = "not_exist_host";
	private String id = NOT_EXIST_HOST;
	private Reservation reservation;

	public SingleReservationHolder()
	{
	}

	@Override
	public Reservation get(String id1)
	{
		if (!id.equals(id1))
		{
			return Reservation.NULL_OBJECT;
		}
		return reservation;
	}

	@Override
	public void put(String id1, Reservation r)
	{
		id = id1;
		reservation = r;
	}

}
