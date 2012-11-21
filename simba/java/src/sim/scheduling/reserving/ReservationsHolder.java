package sim.scheduling.reserving;

import static com.google.common.collect.Maps.*;

import java.util.Map;

public class ReservationsHolder implements Reservations
{
	private Map<String, Reservation> reservations = newHashMap();

	public ReservationsHolder()
	{
	}

	@Override
	public Reservation get(String id1)
	{
		if (!reservations.containsKey(id1))
		{
			return Reservation.NULL_OBJECT;
		}
		return reservations.get(id1);
	}

	@Override
	public void put(String id1, Reservation r)
	{
		reservations.put(id1, r);
	}

}
