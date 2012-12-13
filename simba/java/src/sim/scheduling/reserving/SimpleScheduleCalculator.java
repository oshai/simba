package sim.scheduling.reserving;

import java.util.List;

import com.google.common.collect.Iterables;

public class SimpleScheduleCalculator implements ScheduleCalculator
{
	@Override
	public Iterable<ScheduleCostResult> calculateSchedule(List<ReservingScheduler> schedulers2, final long time)
	{
		return Iterables.transform(schedulers2, new CalculateScheduleFunction(time));
	}
}