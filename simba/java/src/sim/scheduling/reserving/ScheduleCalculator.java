package sim.scheduling.reserving;

import java.util.List;

public interface ScheduleCalculator
{
	public Iterable<ScheduleCostResult> calculateSchedule(List<ReservingScheduler> schedulers2, final long time);
}