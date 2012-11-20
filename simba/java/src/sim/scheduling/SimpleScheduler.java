package sim.scheduling;

import static utils.assertions.Asserter.asserter;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import sim.scheduling.matchers.Matcher;

public class SimpleScheduler implements Scheduler
{
	private final WaitingQueue waitingQueue;
	private final Cluster cluster;
	private final Matcher matcher;
	private final JobDispatcher dispatcher;

	public SimpleScheduler(WaitingQueue waitingQueue, Cluster cluster, Matcher matcher, JobDispatcher dispatcher)
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
			dispatcher.dispatch(job, host, time);
		}
	}
}
