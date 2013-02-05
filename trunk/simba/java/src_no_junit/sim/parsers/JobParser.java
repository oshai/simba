package sim.parsers;

import static sim.parsers.MySplitter.*;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import sim.SimbaConfiguration;
import sim.collectors.AllocationConfiguration;
import sim.event_handling.EventQueue;
import sim.events.Submit;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;
import utils.TextFileUtils;

import com.google.common.base.Predicate;

public class JobParser
{
	private static final Logger log = Logger.getLogger(JobParser.class);
	private static final String JOBS_FILE = System.getProperty("jobs-file");
	private int left;
	private int total;
	private int veryShortJobs;// less than 10 seconds
	private int shortJobs;// less than 10 minutes
	private int mediumJobs;// more than 10 minutes, less than 1 hour
	private int longJobs;// more than one hour
	private int memNarrowJobs;// less than 4 GB
	private int memMediumJobs;// less than 8 GB
	private int memWideJobs;// more than 8 GB
	private int errorLength;// less than 1 sec
	private int errorMemory;// less than 1 GB
	private int parallel;
	private static final int index_actualclassreservation = 21;
	private static final int index_jobid = 0;
	private static final int index_iterationsubmittime = 18;
	private static final int index_startttime = 5;
	private static final int index_wtime = 9;
	private static final int index_utime = 10;
	private static final int index_stime = 11;
	private static final int index_queue = 14;
	private static final int index_qslot = 15;
	private static final int index_cost = 20;
	private static boolean DEBUG = false;

	private final SimbaConfiguration simbaConfiguration;
	private final Cluster cluster;
	private final EventQueue eventQueue;
	private final AllocationConfiguration allocationConfiguration;

	@Inject
	public JobParser(SimbaConfiguration simbaConfiguration, Cluster cluster, EventQueue eventQueue, AllocationConfiguration allocationConfiguration)
	{
		super();
		this.simbaConfiguration = simbaConfiguration;
		this.cluster = cluster;
		this.eventQueue = eventQueue;
		this.allocationConfiguration = allocationConfiguration;
	}

	public void parse()
	{
		log.info("parse() - starting with file " + JOBS_FILE);
		if (DEBUG)
		{
			log.setLevel(Level.DEBUG);
			log.info("parse() - DEBUG MODE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		Predicate<String> predicate = new Predicate<String>()
		{

			@Override
			public boolean apply(String line)
			{
				boolean drop = false;
				total++;
				try
				{
					List<String> cols = splitComma(line);
					double cores = getCores(cols);
					double memory = getMapValue("memory", cols.get(index_actualclassreservation));
					long length = d2l(cols.get(index_wtime));
					if (length < 1)
					{
						length = 1;
						errorLength++;
					}
					if (memory < 1)
					{
						memory = 1;
						errorMemory++;
					}
					updateRunTimeBuckets(length);
					updateMemoryBuckets(memory);
					long submitTime = Math.round(simbaConfiguration.submitRatio() * l(cols.get(index_iterationsubmittime)));
					String id = cols.get(index_jobid);
					if (id.contains(":"))
					{
						parallel++;
						drop = true;
					}
					String qslot = parseQslot(cols);
					// if (!qslot.startsWith("/iil_1base") ||
					// !allocationConfiguration.getAll().containsKey(qslot))
					// {
					// drop = true;
					// }
					double cost = getCost(cols, cores, memory);
					Job job = Job.builder(length).id(id).qslot(qslot).cost(cost).priority(submitTime).submitTime(submitTime).cores(cores).memory(memory).startTime(l(cols.get(index_startttime))).build();
					if (canRun(job) && !drop)
					{
						eventQueue.add(new Submit(job));
						left++;
						log.debug("job is " + job);
					}
					else
					{
						log.debug("apply() - job cannot run " + job.cores() + " " + job.memory());
					}
				}
				catch (Exception ex)
				{
					log.debug("apply() - error: " + ex.getMessage() + "; on line " + line, ex);
				}
				if (JobParser.DEBUG && total > 1000000)
				{
					return false;
				}
				return true;
			}

			private String parseQslot(List<String> cols)
			{
				Iterator<String> qslots1 = MySplitter.splitSlash(cols.get(index_queue) + cols.get(index_qslot)).iterator();
				String $ = "";
				for (int i = 0; i < 2 && qslots1.hasNext(); i++)
				{
					$ += "/" + qslots1.next();
				}
				return $;
			}

		};
		TextFileUtils.getContentByLines(JOBS_FILE, predicate);
		int dropped = total - left;
		log.info("dropped " + getPrecentString(dropped));
		log.info("very short jobs " + getPrecentString(veryShortJobs));
		log.info("short jobs " + getPrecentString(shortJobs));
		log.info("medium jobs " + getPrecentString(mediumJobs));
		log.info("long jobs " + getPrecentString(longJobs));
		log.info("narrow memory jobs " + getPrecentString(memNarrowJobs));
		log.info("medium memory jobs " + getPrecentString(memMediumJobs));
		log.info("wide memory jobs " + getPrecentString(memWideJobs));
		log.info("length error (less than 1 sec) " + getPrecentString(errorLength));
		log.info("memory error (less than 1GB) " + getPrecentString(errorMemory));
		log.info("parallel slaves " + getPrecentString(parallel));
	}

	protected long d2l(String value)
	{
		return Math.round(Double.valueOf(value));
	}

	private String getPrecentString(int jobs)
	{
		return jobs + " jobs which is: " + (int) ((double) jobs * 100 / total) + "%";
	}

	private boolean canRun(Job job)
	{
		for (Host host : cluster.hosts())
		{
			if (host.hasAvailableResourcesFor(job))
			{
				return true;
			}
		}
		return false;
	}

	private double getMapValue(String key, String map)
	{
		for (String entry : splitSemicolon(map.trim()))
		{
			Iterator<String> keyValue = splitEquals(entry).iterator();
			if (key.equals(keyValue.next()))
			{
				return Double.valueOf(keyValue.next());
			}
		}
		throw new RuntimeException("not found in map");
	}

	private long l(String value)
	{
		return Long.valueOf(value);
	}

	private double d(String value)
	{
		return Double.valueOf(value);
	}

	private void updateRunTimeBuckets(long length)
	{
		if (length <= 10)
		{
			veryShortJobs++;
		}
		else if (length <= 60 * 10)
		{
			shortJobs++;
		}
		else if (length <= 60 * 60)
		{
			mediumJobs++;
		}
		else
		// if (length > 60 * 60)
		{
			longJobs++;
		}
	}

	private void updateMemoryBuckets(double memory)
	{
		if (memory <= 4)
		{
			memNarrowJobs++;
		}
		else if (memory <= 8)
		{
			memMediumJobs++;
		}
		else
		{
			memWideJobs++;
		}
	}

	private double getCores(List<String> cols)
	{
		if (simbaConfiguration.isActualCoreUsageSimulation())
		{
			Double stime = Double.valueOf(cols.get(index_stime));
			Double utime = Double.valueOf(cols.get(index_utime));
			Double wtime = Double.valueOf(cols.get(index_wtime));
			return (stime + utime + 0.001) / (wtime + 0.001);
		}
		return getMapValue("cores", cols.get(index_actualclassreservation));
	}

	private double getCost(List<String> cols, double cores, double memory)
	{
		if (simbaConfiguration.isFixedCost())
		{
			return 1;
		}
		return simbaConfiguration.fixedMemory() == null ? d(cols.get(index_cost)) : cores / simbaConfiguration.fixedCores() * memory / simbaConfiguration.fixedMemory() * simbaConfiguration.hostMemoryRatio();
	}
}
