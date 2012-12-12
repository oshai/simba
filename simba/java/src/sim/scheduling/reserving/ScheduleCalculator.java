package sim.scheduling.reserving;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

import sim.model.Host;
import sim.model.Job;

public interface ScheduleCalculator
{
	public Iterable<Pair<String, Map<Job, Host>>> calculateSchedule(List<ReservingScheduler> schedulers2, final long time);
}