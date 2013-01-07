package sim;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.collectors.IJobCollector;
import sim.collectors.IntervalCollector;
import sim.configuration.BestFitConfiguration;
import sim.configuration.ByTraceConfiguration;
import sim.configuration.DistributedSimulationConfiguration;
import sim.configuration.MaxCostConfiguration;
import sim.configuration.ProductionSimbaConfiguration;
import sim.configuration.WorseFitConfiguration;
import sim.event_handling.EventQueue;
import sim.events.Event;
import sim.model.Cluster;
import sim.parsers.HostParser;
import sim.parsers.JobParser;
import sim.scheduling.reserving.IMaxCostCollector;

import com.google.common.base.Stopwatch;
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
		if ("by-trace".equals(System.getProperty("simulation")))
		{
			return new ByTraceConfiguration();
		}
		if ("best-fit".equals(System.getProperty("simulation")))
		{
			return new BestFitConfiguration();
		}
		if ("worse-fit".equals(System.getProperty("simulation")))
		{
			return new WorseFitConfiguration();
		}
		if ("max-cost".equals(System.getProperty("simulation")))
		{
			return new MaxCostConfiguration();
		}
		throw new RuntimeException("please set -Dsimulation properly, it is now: " + System.getProperty("simulation"));
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
		log.info("configuration: " + injector.getInstance(SimbaConfiguration.class));
		Stopwatch stopwatch = new Stopwatch().start();
		parseCluster();
		parseJobs();
		EventQueue eventQueue = createEventQueue();
		Cluster cluster = injector.getInstance(Cluster.class);
		log.info("execute() - cluster size is " + cluster.hosts().size());
		log.info("execute() - # of jobs " + eventQueue.size());
		initCollectors();
		Looper looper = injector.getInstance(Looper.class);
		looper.execute();
		log.info("execute() - finished at " + new Date());
		log.info("execute() - took " + stopwatch.elapsedTime(TimeUnit.SECONDS));
	}

	private void initCollectors()
	{
		injector.getInstance(IJobCollector.class).init();
		injector.getInstance(IMaxCostCollector.class).init();
		injector.getInstance(IntervalCollector.class).init();
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

}
