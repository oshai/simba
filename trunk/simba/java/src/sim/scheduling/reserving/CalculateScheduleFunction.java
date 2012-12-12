package sim.scheduling.reserving;

import java.util.Map;

import org.apache.commons.math3.util.Pair;

import sim.model.Host;
import sim.model.Job;

import com.google.common.base.Function;

public class CalculateScheduleFunction implements Function<ReservingScheduler, Pair<String, Map<Job, Host>>>
{
	private final long time;

	public CalculateScheduleFunction(long time)
	{
		this.time = time;
	}

	@Override
	public Pair<String, Map<Job, Host>> apply(ReservingScheduler arg0)
	{
		return new Pair<String, Map<Job, Host>>(arg0.getClass().getSimpleName(), arg0.scheduleWithoutDispatch(time));
	}
}