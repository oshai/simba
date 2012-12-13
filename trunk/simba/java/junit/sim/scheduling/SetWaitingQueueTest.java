package sim.scheduling;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Iterator;

import org.junit.Test;

import sim.model.Job;

public class SetWaitingQueueTest
{
	@Test
	public void testAdd() throws Exception
	{
		SetWaitingQueue tested = new SetWaitingQueue();
		Job j = mock(Job.class);
		tested.add(j);
		assertTrue(tested.contains(j));
		assertEquals(1, tested.size());
		assertEquals(1, tested.collectAdd());
		assertEquals(0, tested.collectAdd());
		tested.remove(j);
		assertEquals(0, tested.size());
		assertEquals(1, tested.collectRemove());
		assertEquals(0, tested.collectRemove());

	}

	@Test
	public void testAddTwice() throws Exception
	{
		SetWaitingQueue tested = new SetWaitingQueue();
		Job j = mock(Job.class);
		tested.add(j);
		tested.add(j);
		assertEquals(1, tested.collectAdd());
		assertEquals(1, tested.size());

	}

	@Test
	public void testRemoveNotExitent() throws Exception
	{
		SetWaitingQueue tested = new SetWaitingQueue();
		Job j = mock(Job.class);
		assertFalse(tested.remove(j));
		assertEquals(0, tested.collectRemove());
	}

	@Test
	public void testIterable() throws Exception
	{
		SetWaitingQueue tested = new SetWaitingQueue();
		assertFalse(tested.iterator().hasNext());
	}

	@Test
	public void testIterableRemove() throws Exception
	{
		SetWaitingQueue tested = new SetWaitingQueue();
		Job j = mock(Job.class);
		tested.add(j);
		Iterator<Job> iterator = tested.iterator();
		iterator.next();
		iterator.remove();
		assertEquals(1, tested.collectRemove());
	}
}
