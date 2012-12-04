package sim.parsers;

import static sim.parsers.MySplitter.*;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.log4j.Logger;

import sim.Clock;
import sim.SimbaConfiguration;
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
	private int errorLength;// more than 8 GB
	private int errorMemory;// more than 8 GB
	private static final int index_actualclassreservation = 21;// iil-new20
	private static final int index_jobid = 0;// iil-new also
	private static final int index_iterationsubmittime = 18;// iil-new4
	private static final int index_startttime = 5;// iil-new?
	private static final int index_wtime = 9;// iil-new also
	private static boolean DEBUG = false;

	private final SimbaConfiguration simbaConfiguration;

	@Inject
	public JobParser(SimbaConfiguration simbaConfiguration)
	{
		super();
		this.simbaConfiguration = simbaConfiguration;
	}

	public EventQueue parse(Provider<Clock> clockProvider, final Cluster cluster)
	{
		log.info("parse() - starting with file " + JOBS_FILE);
		if (DEBUG)
		{
			log.info("parse() - DEBUG MODE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		final EventQueue $ = new EventQueue(clockProvider);
		Predicate<String> predicate = new Predicate<String>()
		{

			@Override
			public boolean apply(String line)
			{
				total++;
				try
				{
					List<String> cols = splitComma(line);
					double cores = getMapValue("cores", cols.get(index_actualclassreservation)) * simbaConfiguration.jobCoresRatio();
					double memory = getMapValue("memory", cols.get(index_actualclassreservation));
					long length = simbaConfiguration.isBucketSimulation() ? 1 : d2l(cols.get(index_wtime));
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
					long submitTime = l(cols.get(index_iterationsubmittime));
					if (simbaConfiguration.isBucketSimulation())
					{
						submitTime = submitTime / simbaConfiguration.bucketSize() * simbaConfiguration.bucketSize();
					}
					Job job = Job.create(length).id(cols.get(index_jobid)).priority(submitTime).submitTime(submitTime).cores(cores).memory(memory)
							.startTime(l(cols.get(index_startttime))).build();
					if (canRun(job, cluster))
					{
						$.add(new Submit(job));
						left++;
					}
					else
					{
						log.info("apply() - job cannot run " + job.cores() + " " + job.memory());
					}
				}
				catch (Exception ex)
				{
					log.debug("apply() - error: " + ex.getMessage() + "; on line " + line);
				}
				if (JobParser.DEBUG && total > 1000000)
				{
					return false;
				}
				return true;
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
		log.info("length error " + getPrecentString(errorLength));
		log.info("memory error " + getPrecentString(errorMemory));
		return $;
	}

	protected long d2l(String value)
	{
		return Math.round(Double.valueOf(value));
	}

	private String getPrecentString(int jobs)
	{
		return jobs + " jobs which is: " + (int) ((double) jobs * 100 / total) + "%";
	}

	private boolean canRun(Job job, Cluster cluster)
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
}
