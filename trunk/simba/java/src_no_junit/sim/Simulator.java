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
import sim.scheduling.WaitingQueue;
import sim.scheduling.best_fit.BestFitScheduler;
import sim.scheduling.best_fit.SortedHosts;
import sim.scheduling.first_fit.FirstFitScheduler;
import sim.scheduling.matchers.FirstFit;
import sim.scheduling.matchers.Matcher;
import sim.scheduling.mix_fit.MixFitScheduler;
import sim.scheduling.worse_fit.WorseFitScheduler;

import com.intel.swiss.sws.netstar.framework.service.controller.topology.JUnitUtils;

public class Simulator
{
	
	private final Logger log = Logger.getLogger(Simulator.class);
	
	public static void main(String[] args)
	{
		JUnitUtils.disableNativeLibs();
		BasicConfigurator.configure();
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
		Looper looper = createLooperWF(cluster, eventQueue, clock);
		looper.execute();
	}
	
	private Looper createLooperWF(Cluster cluster, EventQueue eventQueue, Clock clock)
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Dispatcher dispatcher = new Dispatcher(eventQueue);
		Scheduler scheduler = new WorseFitScheduler(waitingQueue, cluster, dispatcher);
		JobCollector jobCollector = new JobCollector();
		JobFinisher jobFinisher = new JobFinisher(jobCollector, cluster);
		HostCollector hostCollector = new HostCollector(cluster, 300);
		Looper looper = new Looper(clock, eventQueue, waitingQueue, scheduler, hostCollector, jobFinisher);
		return looper;
	}
	
	private Looper createLooperMF(Cluster cluster, EventQueue eventQueue, Clock clock)
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Dispatcher dispatcher = new Dispatcher(eventQueue);
		Scheduler scheduler = new MixFitScheduler(waitingQueue, cluster, dispatcher);
		JobCollector jobCollector = new JobCollector();
		JobFinisher jobFinisher = new JobFinisher(jobCollector, cluster);
		HostCollector hostCollector = new HostCollector(cluster, 300);
		Looper looper = new Looper(clock, eventQueue, waitingQueue, scheduler, hostCollector, jobFinisher);
		return looper;
	}
	
	private Looper createLooperBF(Cluster cluster, EventQueue eventQueue, Clock clock)
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Matcher matcher = new FirstFit();
		Dispatcher dispatcher = new Dispatcher(eventQueue);
		SortedHosts sortedHosts = new SortedHosts(cluster);
		Scheduler scheduler = new BestFitScheduler(waitingQueue, sortedHosts, matcher, dispatcher);
		JobCollector jobCollector = new JobCollector();
		JobFinisher jobFinisher = new JobFinisher(jobCollector, sortedHosts);
		HostCollector hostCollector = new HostCollector(cluster, 300);
		Looper looper = new Looper(clock, eventQueue, waitingQueue, scheduler, hostCollector, jobFinisher);
		return looper;
	}
	
	private Looper createLooperFF(Cluster cluster, EventQueue eventQueue, Clock clock)
	{
		WaitingQueue waitingQueue = new WaitingQueue();
		Matcher matcher = new FirstFit();
		Dispatcher dispatcher = new Dispatcher(eventQueue);
		Scheduler scheduler = new FirstFitScheduler(waitingQueue, cluster, matcher, dispatcher);
		JobCollector jobCollector = new JobCollector();
		JobFinisher jobFinisher = new JobFinisher(jobCollector, cluster);
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
