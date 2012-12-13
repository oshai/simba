package sim.scheduling.reserving;

import java.util.*;

import org.apache.commons.math3.util.*;

import sim.model.*;

import com.google.common.collect.*;

public class SimpleScheduleCalculator implements ScheduleCalculator
{
	@Override
	public Iterable<Pair<String, Map<Job, Host>>> calculateSchedule(List<ReservingScheduler> schedulers2, final long time)
	{
		return Iterables.transform(schedulers2, new CalculateScheduleFunction(time));
	}
}