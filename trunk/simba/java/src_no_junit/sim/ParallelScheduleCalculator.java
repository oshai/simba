package sim;

import static com.google.common.collect.Lists.*;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import sim.scheduling.reserving.CalculateScheduleFunction;
import sim.scheduling.reserving.ReservingScheduler;
import sim.scheduling.reserving.ScheduleCalculator;
import sim.scheduling.reserving.ScheduleCostResult;

public class ParallelScheduleCalculator implements ScheduleCalculator
{

	private final ExecutorService executor = Executors.newFixedThreadPool(20);

	@Override
	public Iterable<ScheduleCostResult> calculateSchedule(List<ReservingScheduler> schedulers2, long time)
	{
		List<Future<ScheduleCostResult>> list = newArrayList();
		for (int i = 0; i < schedulers2.size(); i++)
		{
			Callable<ScheduleCostResult> worker = new Calc(time, schedulers2.get(i));
			Future<ScheduleCostResult> submit = executor.submit(worker);
			list.add(submit);
		}
		// Now retrieve the result
		List<ScheduleCostResult> $ = newArrayList();
		for (Future<ScheduleCostResult> future : list)
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

	private static class Calc implements Callable<ScheduleCostResult>
	{
		@Override
		public ScheduleCostResult call() throws Exception
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
