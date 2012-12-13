package sim.distributed;

import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.runners.*;

import sim.model.*;
import sim.scheduling.*;

import com.google.common.collect.*;

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
		WaitOnAllHostsDistributedScheduler tested = new WaitOnAllHostsDistributedScheduler(waitingQueue, hostSchedulers, new SetWaitingQueue());
		tested.distributeJobs(time);
		for (HostScheduler hostScheduler : hostSchedulers)
		{
			verify(hostScheduler).addJob(job);
		}
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
