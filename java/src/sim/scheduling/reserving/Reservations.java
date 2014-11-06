package sim.scheduling.reserving;

public interface Reservations
{

	Reservation get(String id1);

	void put(String id1, Reservation r);

	void clear();

}