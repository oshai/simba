package sim;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
import sim.scheduling.ReservingScheduler;
import sim.scheduling.Scheduler;
import sim.scheduling.WaitingQueue;
import sim.scheduling.graders.AvailableMemoryGrader;
import sim.scheduling.graders.Constant;
import sim.scheduling.graders.Grader;
import sim.scheduling.graders.RandomGrader;
import sim.scheduling.matchers.GradeMatcherProvider;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;

public class Simulator
{

	private final Logger log = Logger.getLogger(Simulator.class);

	public Simulator()
	{
	}

	public static void main(String[] args)
	{
		BasicConfigurator.configure();
		Level level = Boolean.getBoolean("ebug") ? Level.DEBUG : Level.INFO;
		Logger.getRootLogger().setLevel(level);
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
		Stopwatch stopwatch = new Stopwatch().start();
		Cluster cluster = new HostParser().parse();
		ClockProvider clockProvider = new ClockProvider();
		EventQueue eventQueue = new JobParser().parse(clockProvider, cluster);
		Event event = eventQueue.peek();
		long time = 0;
		if (null != event && !submitImmediately())
		{
			time = event.time() - 1;
		}
		log.info("execute() - simulation starting clock (epoc): " + time);
		Clock clock = new Clock(time);
		clockProvider.setClock(clock);
		Grader grader = createGrader();
		log.info("execute() - cluster size is " + cluster.hosts().size());
		log.info("execute() - # of jobs " + eventQueue.size());
		log.info("execute() - grader is " + grader.toString());
		Looper looper = createLooper(cluster, eventQueue, clock, grader);
		looper.execute();
		log.info("execute() - finished at " + new Date());
		log.info("execute() - took " + stopwatch.elapsedTime(TimeUnit.SECONDS));
	}

	private Grader createGrader()
	{
		HashMap<String, Grader> graders = Maps.newHashMap();
		graders.put("MF", GradeMatcherProvider.createGraderMf1());
		// graders.put("MF2", GradeMatcherProvider.createGraderMf2());
		// graders.put("MF3", GradeMatcherProvider.createGraderMf3());
		// graders.put("MF4", GradeMatcherProvider.createGraderMf4());
		// graders.put("MF5", GradeMatcherProvider.createGraderMf5());
		// graders.put("MF6", GradeMatcherProvider.createGraderMf6());
		graders.put("SMF", GradeMatcherProvider.createGraderSmf());
		// graders.put("BFI", GradeMatcherProvider.createGraderBfi());
		graders.put("BF", GradeMatcherProvider.createGraderBf2());
		// graders.put("NF", GradeMatcherProvider.createProductionGrader());
		// graders.put("BF", new BestFit()); // specific grader
		graders.put("FF", new Constant(0)); // constant grader
		graders.put("RF", new RandomGrader(100000)); // random grader
		graders.put("WF", new AvailableMemoryGrader()); // specific grader

		Grader $ = graders.get(getMatcherProperty().toUpperCase());
		if (null == $)
		{
			throw new RuntimeException("no matcher for " + getMatcherProperty() + " from: " + graders.keySet());
		}
		return $;
	}

	private String getMatcherProperty()
	{
		return System.getProperty("grader");
	}

	protected Looper createLooper(Cluster cluster, EventQueue eventQueue, Clock clock, Grader grader)
	{
		Dispatcher dispatcher = new Dispatcher(eventQueue);
		WaitingQueue waitingQueue = new WaitingQueue();
		if (submitImmediately())
		{
			moveJobsToWaitQueue(eventQueue, waitingQueue);
		}
		// Scheduler scheduler = new SimpleScheduler(waitingQueue, cluster, new
		// GradeMatcher(grader), dispatcher);
		Scheduler scheduler = new ReservingScheduler(waitingQueue, cluster, grader, dispatcher);
		JobCollector jobCollector = new JobCollector();
		JobFinisher jobFinisher = new JobFinisher(jobCollector);
		HostCollector hostCollector = new HostCollector(cluster, 300, waitingQueue);
		Looper looper = new Looper(clock, eventQueue, waitingQueue, scheduler, hostCollector, jobFinisher);
		return looper;
	}

	private boolean submitImmediately()
	{
		return "immediately".equalsIgnoreCase(getSubmitProperty());
	}

	private String getSubmitProperty()
	{
		return System.getProperty("submit");
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
