package sim;

import javax.inject.Provider;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.collectors.HostCollector;
import sim.collectors.JobCollector;
import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.events.Submit;
import sim.model.Cluster;
import sim.model.Job;
import sim.parsers.HostParser;
import sim.parsers.JobParser;
import sim.scheduling.Dispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.SimpleScheduler;
import sim.scheduling.WaitingQueue;
import sim.scheduling.matchers.BestFit;
import sim.scheduling.matchers.FirstFit;
import sim.scheduling.matchers.Matcher;
import sim.scheduling.matchers.GradeMatcherProvider;
import sim.scheduling.matchers.RandomFit;
import sim.scheduling.matchers.WorseFit;

public class Simulator
{

	private final Logger log = Logger.getLogger(Simulator.class);
	private final String[] args;

	public Simulator(String[] args)
	{
		this.args = args;
	}

	public static void main(String[] args)
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		try
		{
			new Simulator(args).execute();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		System.exit(0);
	}

	private void execute()
	{
		log.info("execute() - starting...");
		Cluster cluster = new HostParser().parse();
		ClockProvider clockProvider = new ClockProvider();
		EventQueue eventQueue = new JobParser().parse(clockProvider, cluster);
		Event event = eventQueue.peek();
		long time = 0;
		if (null != event && !submitImmediately())
		{
			time = event.time() - 1;
		}
		log.info("execute() - starting at " + time);
		Clock clock = new Clock(time);
		clockProvider.setClock(clock);
		Matcher matcher = createMatcher();
		log.info("execute() - cluster size is " + cluster.hosts().size());
		log.info("execute() - # of jobs " + eventQueue.size());
		log.info("execute() - matcher is " + matcher.getClass().getSimpleName());
		Looper looper = createLooper(cluster, eventQueue, clock, matcher);
		looper.execute();
	}

	private Matcher createMatcher()
	{
		if (args[0].equalsIgnoreCase("WF"))
		{
			return new WorseFit();
		}
		if (args[0].equalsIgnoreCase("MF"))
		{
			return GradeMatcherProvider.createGraderMf1();
		}
		if (args[0].equalsIgnoreCase("BF"))
		{
			return new BestFit();
		}
		if (args[0].equalsIgnoreCase("FF"))
		{
			return new FirstFit();
		}
		if (args[0].equalsIgnoreCase("RF"))
		{
			return new RandomFit();
		}
		throw new RuntimeException("no scheduler chosen: WF, MF, BF, FF, RF");
	}

	protected Looper createLooper(Cluster cluster, EventQueue eventQueue, Clock clock, Matcher matcher)
	{
		Dispatcher dispatcher = new Dispatcher(eventQueue);
		WaitingQueue waitingQueue = new WaitingQueue();
		if (submitImmediately())
		{
			moveJobsToWaitQueue(eventQueue, waitingQueue);
		}
		else if (!args[1].equalsIgnoreCase("submit=real"))
		{
			throw new RuntimeException("arg1 should be submit=immediately or submit=real");
		}
		Scheduler scheduler = new SimpleScheduler(waitingQueue, cluster, matcher, dispatcher);
		JobCollector jobCollector = new JobCollector();
		JobFinisher jobFinisher = new JobFinisher(jobCollector);
		HostCollector hostCollector = new HostCollector(cluster, 300);
		Looper looper = new Looper(clock, eventQueue, waitingQueue, scheduler, hostCollector, jobFinisher);
		return looper;
	}

	private boolean submitImmediately()
	{
		return args[1].equalsIgnoreCase("submit=immediately");
	}

	private void moveJobsToWaitQueue(EventQueue eventQueue, WaitingQueue waitingQueue)
	{
		log.info("moveJobsToWaitQueue() - start");
		while (eventQueue.size() > 0)
		{
			Job job = ((Submit) eventQueue.removeFirst()).job();
			Job jobUpdated = Job.create(job.length()).cores(job.cores()).submitTime(0L).memory(job.memory()).id(job.id()).priority(job.priority()).build();
			waitingQueue.add(jobUpdated);
		}
	}

	private final class ClockProvider implements Provider<Clock>
	{
		private Clock clock;

		@Override
		public Clock get()
		{
			return getClock();
		}

		public Clock getClock()
		{
			return clock;
		}

		public void setClock(Clock clock)
		{
			this.clock = clock;
		}
	}
}
