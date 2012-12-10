package sim.scheduling;

import static junit.framework.Assert.*;

import org.junit.Test;

import com.google.common.collect.Lists;

public class AggregatedWaitingQueueTest
{
	@Test
	public void testEmpty() throws Exception
	{
		AggregatedWaitingQueue tested = new AggregatedWaitingQueue(Lists.<HostScheduler> newArrayList());
		assertEquals(0, tested.collectAdd());
		assertEquals(0, tested.collectRemove());
		assertEquals(0, tested.size());
		assertNotNull(tested.iterator());
	}
}
