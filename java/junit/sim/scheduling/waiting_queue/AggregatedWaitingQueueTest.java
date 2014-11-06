package sim.scheduling.waiting_queue;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import sim.distributed.HostScheduler;
import sim.scheduling.waiting_queue.AggregatedWaitingQueue;

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

	@Test
	public void testNotEmpty() throws Exception
	{
		HostScheduler hs = mock(HostScheduler.class);
		when(hs.collectAdd()).thenReturn(1);
		when(hs.collectRemove()).thenReturn(2);
		when(hs.waitingJobsSize()).thenReturn(3);
		AggregatedWaitingQueue tested = new AggregatedWaitingQueue(Lists.<HostScheduler> newArrayList(hs, hs));
		assertEquals(2, tested.collectAdd());
		assertEquals(4, tested.collectRemove());
		assertEquals(6, tested.size());
		assertNotNull(tested.iterator());
	}
}
