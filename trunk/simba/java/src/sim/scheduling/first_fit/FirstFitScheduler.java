package sim.scheduling.first_fit;

import static com.intel.swiss.sws.mechanism.assertions.Asserter.*;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.Dispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.WaitingQueue;
import sim.scheduling.matchers.Matcher;

public class FirstFitScheduler implements Scheduler
{
	private final WaitingQueue waitingQueue;
	private final Cluster cluster;
	private final Matcher matcher;
	private final Dispatcher dispatcher;
	
	public FirstFitScheduler(WaitingQueue waitingQueue, Cluster cluster, Matcher matcher, Dispatcher dispatcher)
	{
		this.cluster = cluster;
		this.waitingQueue = waitingQueue;
		this.matcher = matcher;
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void schedule(long time)
	{
		while (!waitingQueue.isEmpty())
		{
			Job job = waitingQueue.peek();
			Host host = matcher.match(job, cluster.hosts());
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
