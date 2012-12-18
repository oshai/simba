package sim;

import static com.google.common.collect.Lists.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.collectors.IntervalCollector;
import sim.collectors.JobCollector;
import sim.collectors.MaxCostCollector;
import sim.collectors.WaitingQueueStatistics;
import sim.configuration.DistributedSimulationConfiguration;
import sim.configuration.ProductionSimbaConfiguration;
import sim.configuration.ProductionSimbaConfiguration.LooperFactory;
import sim.distributed.ByStrategyHostSelector;
import sim.distributed.ConstantExpandingStrategy;
import sim.distributed.DistributedJobDispatcher;
import sim.distributed.ExpandingDistributedScheduler;
import sim.distributed.HostScheduler;
import sim.distributed.RandomListSelector;
import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.model.Cluster;
import sim.model.Host;
import sim.parsers.HostParser;
import sim.parsers.JobParser;
import sim.scheduling.AbstractWaitingQueue;
import sim.scheduling.ByTraceScheduler;
import sim.scheduling.JobDispatcher;
import sim.scheduling.LinkedListWaitingQueue;
import sim.scheduling.Scheduler;
import sim.scheduling.SetWaitingQueue;
import sim.scheduling.SimpleScheduler;
import sim.scheduling.SortedWaitingQueue;
import sim.scheduling.WaitingQueueForStatistics;
import sim.scheduling.graders.AvailableMemoryGrader;
import sim.scheduling.graders.Constant;
import sim.scheduling.graders.Grader;
import sim.scheduling.graders.RandomGrader;
import sim.scheduling.graders.ThrowingExceptionGrader;
import sim.scheduling.matchers.GradeMatcher;
import sim.scheduling.matchers.GradeMatcherProvider;
import sim.scheduling.reserving.MaxCostScheduler;
import sim.scheduling.reserving.ReservingScheduler;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Simulator
{

	private final Logger log = Logger.getLogger(Simulator.class);
	private final Injector injector;

	public Simulator()
	{
		injector = Guice.createInjector(createConfiguration());
	}

	private ProductionSimbaConfiguration createConfiguration()
	{
		if ("distributed".equals(System.getProperty("simulation")))
		{
			return new DistributedSimulationConfiguration();
		}
		if ("central".equals(System.getProperty("simulation")))
		{
			return new ProductionSimbaConfiguration();
		}
		throw new RuntimeException("please set -Dsimulation");
	}

	public static void main(String[] args)
	{
		BasicConfigurator.configure();
		Level level = Boolean.getBoolean("ebug") ? Level.DEBUG : Level.INFO;
		Logger.getRootLogger().setLevel(level);
		Logger.getLogger("sim").setLevel(level);
		try
		{
			new Simulator().execute();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		System.exit(0);
	}

	private void execute()
	{
		log.info("execute() - starting at " + new Date());
		log.info("configuration: " + getConfiguration());
		Stopwatch stopwatch = new Stopwatch().start();
		parseCluster();
		parseJobs();
		EventQueue eventQueue = createEventQueue();
		Grader grader = createGrader();
		Cluster cluster = injector.getInstance(Cluster.class);
		log.info("execute() - cluster size is " + cluster.hosts().size());
		log.info("execute() - # of jobs " + eventQueue.size());
		log.info("execute() - grader is " + grader.toString());
		Looper looper = createLooper(cluster, eventQueue, injector.getInstance(Clock.class), grader);
		looper.execute();
		log.info("execute() - finished at " + new Date());
		log.info("execute() - took " + stopwatch.elapsedTime(TimeUnit.SECONDS));
	}

	private EventQueue createEventQueue()
	{
		EventQueue eventQueue = injector.getInstance(EventQueue.class);
		Event event = eventQueue.peek();
		long time = event.time() - 1;
		log.info("execute() - simulation starting clock (epoc): " + time);
		injector.getInstance(Clock.class).time(time);
		return eventQueue;
	}

	private void parseJobs()
	{
		injector.getInstance(JobParser.class).parse();
	}

	private void parseCluster()
	{
		injector.getInstance(HostParser.class).parse();
	}

	private Grader createGrader()
	{
		String graderName = getGraderProperty().toUpperCase();
		return getGraderForName(graderName);
	}

	protected Grader getGraderForName(String graderName)
	{
		HashMap<String, Grader> graders = Maps.newHashMap();
		graders.put("MF", GradeMatcherProvider.createGraderMf1());
		// graders.put("MF2", GradeMatcherProvider.createGraderMf2());
		graders.put("MF3", GradeMatcherProvider.createGraderMf3());
		graders.put("MF4", GradeMatcherProvider.createGraderMf4());
		// graders.put("MF5", GradeMatcherProvider.createGraderMf5());
		graders.put("MF6", GradeMatcherProvider.createGraderMf6());
		graders.put("SMF", GradeMatcherProvider.createGraderSmf());
		graders.put("BFI", GradeMatcherProvider.createGraderBfi());
		graders.put("BF", GradeMatcherProvider.createGraderBf2());
		graders.put("NF", GradeMatcherProvider.createProductionGrader());
		// graders.put("BF", new BestFit()); // specific grader
		graders.put("FF", new Constant(0)); // constant grader
		graders.put("RF", new RandomGrader(100000)); // random grader
		graders.put("WF", new AvailableMemoryGrader()); // specific grader
		graders.put("BT", new ThrowingExceptionGrader());
		graders.put("DISTRIBUTED", new ThrowingExceptionGrader());

		Grader $ = graders.get(graderName);
		if (null == $)
		{
			throw new RuntimeException("no matcher for " + getGraderProperty() + " from: " + graders.keySet());
		}
		return $;
	}

	private String getGraderProperty()
	{
		return System.getProperty("grader");
	}

	private String getSchedulerProperty()
	{
		return System.getProperty("scheduler");
	}

	private String getWaitingQueueProperty()
	{
		return System.getProperty("waitQueue");
	}

	protected Looper createLooper(Cluster cluster, EventQueue eventQueue, Clock clock, Grader grader)
	{
		JobDispatcher dispatcher = new JobDispatcher(eventQueue);
		AbstractWaitingQueue waitingQueue = new LinkedListWaitingQueue();
		if (isSortedWaitingQueue())
		{
			waitingQueue = new SortedWaitingQueue();
		}
		ArrayList<HostScheduler> hostSchedulers = null;
		SetWaitingQueue distributedWaitingJobs = new SetWaitingQueue();
		if (isDistributed())
		{
			dispatcher = new DistributedJobDispatcher(eventQueue, distributedWaitingJobs);
			hostSchedulers = createHostSchedulers(cluster, dispatcher, distributedWaitingJobs);
		}
		WaitingQueueForStatistics waitingQueueForStatistics = isDistributed() ? distributedWaitingJobs : waitingQueue;
		log.info("wait queue is " + waitingQueueForStatistics.getClass().getSimpleName());
		WaitingQueueStatistics waitingQueueStatistics = new WaitingQueueStatistics(waitingQueueForStatistics, Integer.MAX_VALUE, clock);
		Scheduler scheduler = createSchduler(cluster, grader, dispatcher, waitingQueue, hostSchedulers, distributedWaitingJobs);
		log.info("createLooper() - scheduler is " + scheduler.getClass().getSimpleName());
		JobCollector jobCollector = new JobCollector();
		JobFinisher jobFinisher = new JobFinisher(jobCollector);
		int collectTime = getConfiguration().isBucketSimulation() ? (int) getConfiguration().bucketSize() : 300;
		IntervalCollector hostCollector = new IntervalCollector(cluster, collectTime, waitingQueueStatistics, jobFinisher);
		Looper looper = injector.getInstance(LooperFactory.class).create(clock, eventQueue, waitingQueue, scheduler, hostCollector, jobFinisher);

		// Looper looper = injector.getI;// new Looper(clock, eventQueue,
		// // waitingQueue, scheduler,
		// hostCollector, jobFinisher);
		return looper;
	}

	private ArrayList<HostScheduler> createHostSchedulers(Cluster cluster, final JobDispatcher dispatcher, final SetWaitingQueue distributedWaitingJobs)
	{
		return newArrayList(Collections2.transform(cluster.hosts(), new Function<Host, HostScheduler>()
		{
			@Override
			public HostScheduler apply(Host host)
			{
				return new HostScheduler(host, dispatcher, new LinkedListWaitingQueue(), distributedWaitingJobs);
			}
		}));
	}

	private SimbaConfiguration getConfiguration()
	{
		return injector.getInstance(SimbaConfiguration.class);
	}

	private boolean isSortedWaitingQueue()
	{
		return "sorted".equals(getWaitingQueueProperty());
	}

	private Scheduler createSchduler(Cluster cluster, Grader grader, final JobDispatcher dispatcher, AbstractWaitingQueue waitingQueue, ArrayList<HostScheduler> hostSchedulers, SetWaitingQueue distributedWaitingJobs)
	{
		if (isSortedWaitingQueue())
		{
			return new LoadedMachinesFirstScheduler(waitingQueue, cluster, grader, dispatcher);
		}
		if ("fifo".equals(getSchedulerProperty()))
		{
			return new SimpleScheduler(waitingQueue, cluster, new GradeMatcher(grader), dispatcher);
		}
		if ("reservation".equals(getSchedulerProperty()))
		{
			return new ReservingScheduler(waitingQueue, cluster, grader, dispatcher, getConfiguration());
		}
		if ("by-trace".equals(getSchedulerProperty()))
		{
			return new ByTraceScheduler(waitingQueue, cluster, dispatcher);
		}
		if ("max-cost".equals(getSchedulerProperty()))
		{
			List<ReservingScheduler> l = newArrayList(new ReservingScheduler(waitingQueue, cluster, getGraderForName("BF"), dispatcher, getConfiguration()), new ReservingScheduler(waitingQueue, cluster, getGraderForName("WF"), dispatcher, getConfiguration()), new ReservingScheduler(waitingQueue, cluster, getGraderForName("MF"), dispatcher, getConfiguration()), new ReservingScheduler(waitingQueue, cluster, getGraderForName("FF"), dispatcher, getConfiguration()), new ReservingScheduler(waitingQueue, cluster, getGraderForName("NF"), dispatcher, getConfiguration()), new ReservingScheduler(waitingQueue, cluster, getGraderForName("MF3"), dispatcher, getConfiguration()), new ReservingScheduler(waitingQueue, cluster, getGraderForName("MF4"), dispatcher, getConfiguration()), new ReservingScheduler(waitingQueue, cluster, getGraderForName("MF6"), dispatcher, getConfiguration()), new ReservingScheduler(waitingQueue, cluster, getGraderForName("SMF"), dispatcher, getConfiguration()), new ReservingScheduler(waitingQueue, cluster, getGraderForName("RF"), dispatcher, getConfiguration()));
			return new MaxCostScheduler(waitingQueue, cluster, grader, dispatcher, getConfiguration(), l, new ParallelScheduleCalculator(), new MaxCostCollector());
		}
		if (isDistributed())
		{
			// ArrayList<HostScheduler> hostsSched = hostSchedulers;
			// HostSelector hostSelector1 = new HostSelector(hostsSched);
			// return new WaitOnAllHostsDistributedScheduler(waitingQueue,
			// hostsSched, distributedWaitingJobs);
			return new ExpandingDistributedScheduler(waitingQueue, hostSchedulers, new ByStrategyHostSelector(hostSchedulers, new RandomListSelector()), distributedWaitingJobs, (DistributedSimbaConfiguration) getConfiguration(), new ConstantExpandingStrategy((DistributedSimbaConfiguration) getConfiguration()));
		}
		throw new RuntimeException("no scheduler " + getSchedulerProperty());
	}

	private boolean isDistributed()
	{
		return "distributed".equals(getSchedulerProperty());
	}
}
