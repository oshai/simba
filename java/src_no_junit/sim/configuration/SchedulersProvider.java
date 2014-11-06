package sim.configuration;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import sim.SimbaConfiguration;
import sim.model.Cluster;
import sim.scheduling.JobDispatcher;
import sim.scheduling.graders.AvailableMemoryGrader;
import sim.scheduling.graders.Constant;
import sim.scheduling.graders.Grader;
import sim.scheduling.graders.RandomGrader;
import sim.scheduling.graders.ThrowingExceptionGrader;
import sim.scheduling.matchers.GradeMatcherProvider;
import sim.scheduling.reserving.ReservingScheduler;
import sim.scheduling.waiting_queue.WaitingQueue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SchedulersProvider implements Provider<List<ReservingScheduler>>
{

	private final WaitingQueue waitingQueue;
	private final SimbaConfiguration simbaConfiguration;
	private final JobDispatcher dispatcher;
	private final Cluster cluster;

	@Inject
	public SchedulersProvider(WaitingQueue waitingQueue, SimbaConfiguration simbaConfiguration, JobDispatcher dispatcher, Cluster cluster)
	{
		super();
		this.waitingQueue = waitingQueue;
		this.simbaConfiguration = simbaConfiguration;
		this.dispatcher = dispatcher;
		this.cluster = cluster;
	}

	@Override
	public List<ReservingScheduler> get()
	{
		List<ReservingScheduler> l = Lists.newArrayList(createScheduler("BEST-FIT"), createScheduler("WF"), createScheduler("BFC"), createScheduler("WFC"), createScheduler("MF4"), createScheduler("mem_first_rev"), createScheduler("mem_first"));
		return l;
	}

	protected ReservingScheduler createScheduler(String graderName)
	{
		return new ReservingScheduler(waitingQueue, cluster, getGraderForName(graderName), dispatcher, simbaConfiguration);
	}

	private Grader getGraderForName(String graderName)
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
		graders.put("BEST-FIT", GradeMatcherProvider.createGraderBf2());
		graders.put("BFC", GradeMatcherProvider.createGraderBfCores());
		graders.put("WFC", GradeMatcherProvider.createGraderWfCores());
		graders.put("mem_first_rev", GradeMatcherProvider.createProductionGraderMemFirstRev());
		graders.put("mem_first", GradeMatcherProvider.createProductionGraderMemFirst());
		// graders.put("BF", new BestFit()); // specific grader
		graders.put("FF", new Constant(0)); // constant grader
		graders.put("RF", new RandomGrader(100000)); // random grader
		graders.put("WF", new AvailableMemoryGrader()); // specific grader
		graders.put("BY-TRACE", new ThrowingExceptionGrader());
		graders.put("MAX-COST", new ThrowingExceptionGrader());
		graders.put("DISTRIBUTED", new ThrowingExceptionGrader());

		Grader $ = graders.get(graderName);
		if (null == $)
		{
			throw new RuntimeException("no matcher for " + graderName + " from: " + graders.keySet());
		}
		return $;
	}
}