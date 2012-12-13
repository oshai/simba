package sim.distributed;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import sim.model.Job;
import sim.scheduling.LinkedListWaitingQueue;
import sim.scheduling.SetWaitingQueue;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class WaitOnAllHostsDistributedSchedulerTest
{

	@Test
	public void testSchedule()
	{
		long time = 17;
		ArrayList<HostScheduler> hostSchedulers = createHostSchedulers(2);
		LinkedListWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		Job job = mock(Job.class);
		waitingQueue.add(job);
		WaitOnAllHostsDistributedScheduler tested = createScheduler(hostSchedulers, waitingQueue);
		tested.distributeJobs(time);
		for (HostScheduler hostScheduler : hostSchedulers)
		{
			verify(hostScheduler).addJob(job);
		}
	}

	private WaitOnAllHostsDistributedScheduler createScheduler(ArrayList<HostScheduler> hostSchedulers, LinkedListWaitingQueue waitingQueue)
	{
		WaitOnAllHostsDistributedScheduler tested = new WaitOnAllHostsDistributedScheduler(waitingQueue, hostSchedulers, new SetWaitingQueue());
		return tested;
	}

	@Test
	public void testDoNothing()
	{
		WaitOnAllHostsDistributedScheduler tested = createScheduler(null, null);
		tested.scheduleWaitingJobsAgain(0);
	}

	private ArrayList<HostScheduler> createHostSchedulers(int size)
	{
		ArrayList<HostScheduler> $ = Lists.<HostScheduler> newArrayList();
		for (int i = 0; i < size; i++)
		{
			$.add(mock(HostScheduler.class));
		}
		return $;
	}
}
