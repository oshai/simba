package sim.scheduling.reserving;

import static org.junit.Assert.*;

import org.junit.Test;

public class SingleReservationHolderTest
{

	@Test
	public void test()
	{
		SingleReservationHolder tested = new SingleReservationHolder();
		Reservation r = new Reservation(1.0, 1.0);
		tested.put("id", r);
		assertEquals(r, tested.get("id"));
		assertEquals(Reservation.NULL_OBJECT, tested.get("bla"));
		tested.clear();
		assertEquals(Reservation.NULL_OBJECT, tested.get("id"));

	}

	@Test(expected = RuntimeException.class)
	public void testNo2Reservations()
	{
		SingleReservationHolder tested = new SingleReservationHolder();
		Reservation r = new Reservation(1.0, 1.0);
		tested.put("id", r);
		tested.put("id", r);
	}

}
