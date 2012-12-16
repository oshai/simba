package sim.distributed;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;

import sim.model.Job;

public class ByStrategyHostSelectorTest
{
	@Test
	public void testSelect()
	{
		HostScheduler h = mock(HostScheduler.class);
		List<HostScheduler> hostScheduler = newArrayList(h);
		Job job = mock(Job.class);
		ByStrategyHostSelector tested = new ByStrategyHostSelector(hostScheduler, new SequentialListSelector());
		assertEquals(h, tested.select(job));
	}

	@Test
	public void testSelectWhenThereIsNoMoreWillReturnNull()
	{
		HostScheduler h = mock(HostScheduler.class);
		List<HostScheduler> hostScheduler = newArrayList(h);
		Job job = mock(Job.class);
		ByStrategyHostSelector tested = new ByStrategyHostSelector(hostScheduler, new SequentialListSelector());
		assertEquals(h, tested.select(job));
		assertEquals(null, tested.select(job));
	}

	@Test
	public void testSelectTwoJobsOnSameHost()
	{
		HostScheduler h = mock(HostScheduler.class);
		List<HostScheduler> hostScheduler = newArrayList(h);
		Job job = mock(Job.class);
		Job job2 = mock(Job.class);
		ByStrategyHostSelector tested = new ByStrategyHostSelector(hostScheduler, new SequentialListSelector());
		assertEquals(h, tested.select(job));
		assertEquals(h, tested.select(job2));
	}

	@Test
	public void testSelectTwiceShouldGiveSecondHost()
	{
		HostScheduler h = mock(HostScheduler.class);
		HostScheduler h1 = mock(HostScheduler.class);
		List<HostScheduler> hostScheduler = newArrayList(h, h1);
		Job job = mock(Job.class);
		ByStrategyHostSelector tested = new ByStrategyHostSelector(hostScheduler, new SequentialListSelector());
		assertEquals(h, tested.select(job));
		assertEquals(h1, tested.select(job));
	}

}
