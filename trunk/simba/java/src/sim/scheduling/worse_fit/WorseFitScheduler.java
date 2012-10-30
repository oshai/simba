package sim.scheduling.worse_fit;

import static com.intel.swiss.sws.mechanism.assertions.Asserter.*;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.Dispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.WaitingQueue;

public class WorseFitScheduler implements Scheduler
{
	private final WaitingQueue waitingQueue;
	private final Dispatcher dispatcher;
	private final WFHostPicker hostPicker;
	
	public WorseFitScheduler(WaitingQueue waitingQueue, Cluster hosts, Dispatcher dispatcher)
	{
		this.waitingQueue = waitingQueue;
		this.dispatcher = dispatcher;
		this.hostPicker = new WFHostPicker(hosts.hosts());
	}
	
	@Override
	public void schedule(long time)
	{
		while (!waitingQueue.isEmpty())
		{
			Job job = waitingQueue.peek();
			Host host = hostPicker.getBestHost(job);
			if (null == host)
			{
				return;
			}
			Job jobRemoved = waitingQueue.remove();
			asserter().assertEquals(job, jobRemoved, "someone is modifying queue while scheduling");
			dispatcher.dipatch(job, host, time);
		}
	}
	
}
