package sim.scheduling.best_fit;

import static utils.assertions.Asserter.*;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.Dispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.WaitingQueue;
import sim.scheduling.matchers.Matcher;

public class BestFitScheduler implements Scheduler
{
	private final WaitingQueue waitingQueue;
	private final Matcher matcher;
	private final Dispatcher dispatcher;
	private final SortedHosts hosts;
	
	public BestFitScheduler(WaitingQueue waitingQueue, SortedHosts hosts, Matcher matcher, Dispatcher dispatcher)
	{
		this.waitingQueue = waitingQueue;
		this.matcher = matcher;
		this.dispatcher = dispatcher;
		this.hosts = hosts;
	}
	
	@Override
	public void schedule(long time)
	{
		while (!waitingQueue.isEmpty())
		{
			Job job = waitingQueue.peek();
			Host host = matcher.match(job, hosts.hosts());
			if (null == host)
			{
				return;
			}
			Job jobRemoved = waitingQueue.remove();
			asserter().assertEquals(job, jobRemoved, "someone is modifying queue while scheduling");
			hosts.beforeUpdate(host);
			dispatcher.dipatch(job, host, time);
			hosts.afterUpdate(host);
		}
	}
	
}
