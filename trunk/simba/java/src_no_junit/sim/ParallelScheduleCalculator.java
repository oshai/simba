package sim;

import static com.google.common.collect.Lists.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.reserving.CalculateScheduleFunction;
import sim.scheduling.reserving.ReservingScheduler;
import sim.scheduling.reserving.ScheduleCalculator;

public class ParallelScheduleCalculator implements ScheduleCalculator
{

	@Override
	public Iterable<Pair<String, Map<Job, Host>>> calculateSchedule(List<ReservingScheduler> schedulers2, long time)
	{
		ArrayList<Thread> threads = newArrayList();
		ArrayList<Calc> calcs = newArrayList();
		List<Pair<String, Map<Job, Host>>> $ = newArrayList();
		for (ReservingScheduler reservingScheduler : schedulers2)
		{
			Calc c = new Calc(time, reservingScheduler);
			calcs.add(c);
			Thread t = new Thread(c);
			threads.add(t);
			t.start();
		}
		for (Thread t : threads)
		{
			try
			{
				t.join();
			}
			catch (InterruptedException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		for (Calc c : calcs)
		{
			$.add(c.$());
		}
		return $;
	}

	private static class Calc implements Runnable
	{

		long time;
		ReservingScheduler r;
		Pair<String, Map<Job, Host>> $;

		public Calc(long time, ReservingScheduler r)
		{
			super();
			this.time = time;
			this.r = r;
		}

		public Pair<String, Map<Job, Host>> $()
		{
			return $;
		}

		@Override
		public void run()
		{
			$ = new CalculateScheduleFunction(time).apply(r);
		}

	}

}
