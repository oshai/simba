package sim;

import javax.inject.Provider;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import sim.collectors.HostCollector;
import sim.collectors.JobCollector;
import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.model.Cluster;
import sim.parsers.HostParser;
import sim.parsers.JobParser;
import sim.scheduling.Dispatcher;
import sim.scheduling.Scheduler;
import sim.scheduling.SimpleScheduler;
import sim.scheduling.WaitingQueue;
import sim.scheduling.matchers.BestFit;
import sim.scheduling.matchers.FirstFit;
import sim.scheduling.matchers.Matcher;
import sim.scheduling.matchers.MixFit;
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
		if (null != event)
		{
			time = event.time() - 1;
		}
		log.info("execute() - starting at " + time);
		Clock clock = new Clock(time);
		clockProvider.setClock(clock);
		log.info("execute() - cluster size is " + cluster.hosts().size());
		log.info("execute() - # of jobs " + eventQueue.size());
		Looper looper = createLooper(cluster, eventQueue, clock);
		looper.execute();
	}

	private Looper createLooper(Cluster cluster, EventQueue eventQueue, Clock clock)
	{
		if (args[0].equalsIgnoreCase("WF"))
		{
			return createLooperWF(cluster, eventQueue, clock);
		}
		if (args[0].equalsIgnoreCase("MF"))
		{
			return createLooperMF(cluster, eventQueue, clock);
		}
		if (args[0].equalsIgnoreCase("BF"))
		{
			return createLooperBF(cluster, eventQueue, clock);
		}
		if (args[0].equalsIgnoreCase("FF"))
		{
			return createLooperFF(cluster, eventQueue, clock);
		}
		if (args[0].equalsIgnoreCase("RF"))
		{
			return createLooperRF(cluster, eventQueue, clock);
		}
		throw new RuntimeException("no scheduler choosen: WF, MF, BF, FF, RF");
	}

	private Looper createLooperWF(Cluster cluster, EventQueue eventQueue, Clock clock)
	{
		return createLooper(cluster, eventQueue, clock, new WorseFit());
	}

	private Looper createLooperRF(Cluster cluster, EventQueue eventQueue, Clock clock)
	{
		return createLooper(cluster, eventQueue, clock, new RandomFit());
	}

	private Looper createLooperMF(Cluster cluster, EventQueue eventQueue, Clock clock)
	{
		return createLooper(cluster, eventQueue, clock, new MixFit());
	}

	private Looper createLooperBF(Cluster cluster, EventQueue eventQueue, Clock clock)
	{
		return createLooper(cluster, eventQueue, clock, new BestFit());
	}

	private Looper createLooperFF(Cluster cluster, EventQueue eventQueue, Clock clock)
	{
		return createLooper(cluster, eventQueue, clock, new FirstFit());
	}

	protected Looper createLooper(Cluster cluster, EventQueue eventQueue, Clock clock, Matcher matcher)
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Dispatcher dispatcher = new Dispatcher(eventQueue);
		Scheduler scheduler = new SimpleScheduler(waitingQueue, cluster, matcher, dispatcher);
		JobCollector jobCollector = new JobCollector();
		JobFinisher jobFinisher = new JobFinisher(jobCollector);
		HostCollector hostCollector = new HostCollector(cluster, 300);
		Looper looper = new Looper(clock, eventQueue, waitingQueue, scheduler, hostCollector, jobFinisher);
		return looper;
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
