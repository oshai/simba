package sim;

import static com.google.common.collect.Lists.*;

import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.*;

import sim.collectors.*;
import sim.configuration.*;
import sim.configuration.ProductionSimbaConfiguration.LooperFactory;
import sim.distributed.*;
import sim.event_handling.*;
import sim.events.*;
import sim.model.*;
import sim.parsers.*;
import sim.scheduling.*;
import sim.scheduling.graders.*;
import sim.scheduling.matchers.*;
import sim.scheduling.reserving.*;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.inject.*;

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
		long time = 0;
		if (null != event && !submitImmediately())
		{
			time = event.time() - 1;
		}
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
			hostSchedulers = createHostSchedulers(cluster, dispatcher);
		}
		WaitingQueueForStatistics waitingQueueForStatistics = isDistributed() ? distributedWaitingJobs : waitingQueue;
		log.info("wait queue is " + waitingQueueForStatistics.getClass().getSimpleName());
		WaitingQueueStatistics waitingQueueStatistics = new WaitingQueueStatistics(waitingQueueForStatistics, Integer.MAX_VALUE, clock);
		if (submitImmediately())
		{
			moveJobsToWaitQueue(eventQueue, waitingQueue);
		}
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

	private ArrayList<HostScheduler> createHostSchedulers(Cluster cluster, final JobDispatcher dispatcher)
	{
		return newArrayList(Collections2.transform(cluster.hosts(), new Function<Host, HostScheduler>()
		{

			@Override
			public HostScheduler apply(Host host)
			{
				return new HostScheduler(host, dispatcher, new LinkedListWaitingQueue());
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
			return new MaxCostScheduler(waitingQueue, cluster, grader, dispatcher, getConfiguration(), l, new ParallelScheduleCalculator());
		}
		if (isDistributed())
		{
			ArrayList<HostScheduler> hostsSched = hostSchedulers;
			HostSelector hostSelector1 = new HostSelector(hostsSched);
			return new ExpandingDistributedScheduler(waitingQueue, hostsSched, hostSelector1, distributedWaitingJobs);
		}
		throw new RuntimeException("no scheduler " + getSchedulerProperty());
	}

	private boolean isDistributed()
	{
		return "distributed".equals(getSchedulerProperty());
	}

	private boolean submitImmediately()
	{
		return "immediately".equalsIgnoreCase(getSubmitProperty());
	}

	private String getSubmitProperty()
	{
		return System.getProperty("submit");
	}

	private void moveJobsToWaitQueue(EventQueue eventQueue, AbstractWaitingQueue waitingQueue)
	{
		log.info("moveJobsToWaitQueue() - start");
		while (eventQueue.size() > 0)
		{
			Job job = ((Submit) eventQueue.removeFirst()).job();
			Job jobUpdated = Job.builder(job.length()).cores(job.cores()).submitTime(0L).memory(job.memory()).id(job.id()).priority(job.priority()).build();
			waitingQueue.add(jobUpdated);
		}
	}
}
