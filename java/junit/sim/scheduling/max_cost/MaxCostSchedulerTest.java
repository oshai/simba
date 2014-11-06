package sim.scheduling.max_cost;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import sim.SimbaConfiguration;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.graders.Grader;
import sim.scheduling.graders.ThrowingExceptionGrader;
import sim.scheduling.reserving.ReservingScheduler;

import com.google.common.collect.Lists;

public class MaxCostSchedulerTest
{
	@Test(expected = IllegalArgumentException.class)
	public void testEmpty() throws Exception
	{
		List<ReservingScheduler> schedulers = Lists.<ReservingScheduler> newArrayList();
		createSchduler(schedulers);
	}

	@Test
	public void testSimple() throws Exception
	{
		Map<Job, Host> map = newHashMap();
		map.put(Job.builder(1).cost(1).build(), null);
		List<ReservingScheduler> schedulers = newArrayList(createScheduler(map));
		MaxCostScheduler tested = createSchduler(schedulers);
		assertEquals(map, tested.selectJobsToDispatch(0));
	}

	private MaxCostScheduler createSchduler(List<ReservingScheduler> schedulers)
	{
		MaxCostScheduler tested = new MaxCostScheduler(null, null, null, null, mock(SimbaConfiguration.class), schedulers, new SimpleScheduleCalculator(), IMaxCostCollector.NO_OP);
		return tested;
	}

	@Test
	public void test2() throws Exception
	{
		Map<Job, Host> map = newHashMap();
		map.put(Job.builder(1).cost(1).build(), null);
		Map<Job, Host> map2 = newHashMap();
		map2.put(Job.builder(1).cost(1).build(), null);
		map2.put(Job.builder(1).cost(1).build(), null);
		List<ReservingScheduler> schedulers = newArrayList(createScheduler(map), createScheduler(map2));
		MaxCostScheduler tested = createSchduler(schedulers);
		assertEquals(map2, tested.selectJobsToDispatch(0));
	}

	@Test
	public void testTwoWinners() throws Exception
	{
		Map<Job, Host> map = newHashMap();
		map.put(Job.builder(1).cost(1).build(), null);
		List<ReservingScheduler> schedulers = newArrayList(createScheduler(map), createScheduler(map));
		MaxCostScheduler tested = createSchduler(schedulers);
		assertEquals(map, tested.selectJobsToDispatch(0));
	}

	protected ReservingScheduler createScheduler(final Map<Job, Host> map)
	{
		ReservingScheduler s = new ReservingScheduler(null, null, null, null, mock(SimbaConfiguration.class))
		{
			@Override
			public Map<Job, Host> scheduleWithoutDispatch(long time)
			{
				return map;
			}

			@Override
			public Grader grader()
			{
				return new ThrowingExceptionGrader();
			}
		};
		return s;
	}
}
