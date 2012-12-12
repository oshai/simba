package sim.scheduling.reserving;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

import sim.model.Host;
import sim.model.Job;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class SimpleScheduleCalculator implements ScheduleCalculator
{
	@Override
	public Iterable<Pair<String, Map<Job, Host>>> calculateSchedule(List<ReservingScheduler> schedulers2, final long time)
	{
		return Iterables.transform(schedulers2, new CalculateScheduleFunction(time));
	}
}