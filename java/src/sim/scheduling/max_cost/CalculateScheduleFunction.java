package sim.scheduling.max_cost;

import java.util.Map;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.reserving.ReservingScheduler;
import sim.scheduling.reserving.ScheduleCostResult;

import com.google.common.base.Function;

public class CalculateScheduleFunction implements Function<ReservingScheduler, ScheduleCostResult>
{
	private final long time;

	public CalculateScheduleFunction(long time)
	{
		this.time = time;
	}

	@Override
	public ScheduleCostResult apply(ReservingScheduler sched)
	{
		Map<Job, Host> map = sched.scheduleWithoutDispatch(time);
		double cost = calcCost(map);
		return new ScheduleCostResult(sched.grader().toString(), cost, map);
	}

	private double calcCost(Map<Job, Host> current)
	{
		double $ = 0;
		for (Job job : current.keySet())
		{
			$ += job.cost();
		}
		return $;
	}
}