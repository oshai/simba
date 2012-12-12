package sim;

import static com.google.common.collect.Lists.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.math3.util.Pair;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.reserving.CalculateScheduleFunction;
import sim.scheduling.reserving.ReservingScheduler;
import sim.scheduling.reserving.ScheduleCalculator;

public class ParallelScheduleCalculator implements ScheduleCalculator
{

	private final ExecutorService executor = Executors.newFixedThreadPool(20);

	@Override
	public Iterable<Pair<String, Map<Job, Host>>> calculateSchedule(List<ReservingScheduler> schedulers2, long time)
	{
		List<Future<Pair<String, Map<Job, Host>>>> list = new ArrayList<Future<Pair<String, Map<Job, Host>>>>();
		for (int i = 0; i < schedulers2.size(); i++)
		{
			Callable<Pair<String, Map<Job, Host>>> worker = new Calc(time, schedulers2.get(i));
			Future<Pair<String, Map<Job, Host>>> submit = executor.submit(worker);
			list.add(submit);
		}
		// Now retrieve the result
		List<Pair<String, Map<Job, Host>>> $ = newArrayList();
		for (Future<Pair<String, Map<Job, Host>>> future : list)
		{
			try
			{
				$.add(future.get());
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		// executor.shutdown();
		return $;
	}

	private static class Calc implements Callable<Pair<String, Map<Job, Host>>>
	{
		@Override
		public Pair<String, Map<Job, Host>> call() throws Exception
		{
			return new CalculateScheduleFunction(time).apply(r);
		}

		long time;
		ReservingScheduler r;

		public Calc(long time, ReservingScheduler r)
		{
			super();
			this.time = time;
			this.r = r;
		}

	}

}
