package sim.scheduling.max_cost;

import java.util.List;

import sim.scheduling.reserving.ReservingScheduler;
import sim.scheduling.reserving.ScheduleCostResult;

public interface ScheduleCalculator
{
	public Iterable<ScheduleCostResult> calculateSchedule(List<ReservingScheduler> schedulers2, final long time);
}