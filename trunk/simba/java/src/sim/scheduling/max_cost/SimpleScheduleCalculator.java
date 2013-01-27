package sim.scheduling.max_cost;

import java.util.List;

import sim.scheduling.reserving.ReservingScheduler;
import sim.scheduling.reserving.ScheduleCostResult;

import com.google.common.collect.Iterables;

public class SimpleScheduleCalculator implements ScheduleCalculator
{
	@Override
	public Iterable<ScheduleCostResult> calculateSchedule(List<ReservingScheduler> schedulers2, final long time)
	{
		return Iterables.transform(schedulers2, new CalculateScheduleFunction(time));
	}
}