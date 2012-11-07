package sim.parsers;

import javax.inject.Provider;

import org.apache.log4j.Logger;

import sim.Clock;
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
	private static final int index_actualclassreservation = 22 - 1;
	private static final int index_jobid = 1 - 1;
	private static final int index_iterationsubmittime = 19 - 1;
	private static final int index_starttime = 6 - 1;
	private static final int index_finishtime = 7 - 1;

	public EventQueue parse(Provider<Clock> clockProvider, final Cluster cluster)
	{
		log.info("parse() - starting with file " + JOBS_FILE);
		final EventQueue $ = new EventQueue(clockProvider);
		Predicate<String> predicate = new Predicate<String>()
		{
			@Override
			public boolean apply(String line)
			{
				total++;
				try
				{
					String[] cols = line.split(",");
					double cores = getMapValue("cores", cols[index_actualclassreservation]);
					double memory = getMapValue("memory", cols[index_actualclassreservation]);
					long length = l(cols[index_finishtime]) - l(cols[index_starttime]);
					if (length < 10)// when this is 10 it gives bad results,
									// check why!
					{
						length = 10;
						veryShortJobs++;
					}
					else if (length < 60 * 10)
					{
						shortJobs++;
					}
					else if (length < 60 * 60)
					{
						mediumJobs++;
					}
					else if (length > 60 * 60)
					{
						longJobs++;
					}
					Job job = Job.create(length).id(cols[index_jobid]).priority(l(cols[index_starttime])).submitTime(l(cols[index_iterationsubmittime]))
							.cores(cores).memory(memory).build();
					if (canRun(job, cluster))
					{
						$.add(new Submit(job));
						left++;
					}
					else
					{
						log.debug("apply() - job cannot run " + job.cores() + " " + job.memory());
					}
				}
				catch (Exception ex)
				{
					log.debug("apply() - error: " + ex.getMessage() + "; on line " + line);
				}
				return true;
			}

		};
		TextFileUtils.getContentByLines(JOBS_FILE, predicate);
		int dropped = total - left;
		log.info("parse() - dropped " + dropped + " which is: " + (int) ((double) dropped * 100 / total) + "%");
		log.info("parse() - very short jobs " + getPrecentString(veryShortJobs));
		log.info("parse() - short jobs " + getPrecentString(shortJobs));
		log.info("parse() - medium jobs " + getPrecentString(mediumJobs));
		log.info("parse() - long jobs " + getPrecentString(longJobs));
		return $;
	}

	private String getPrecentString(int jobs)
	{
		return jobs + " which is: " + (int) ((double) jobs * 100 / total) + "%";
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
		String[] keyValues = map.trim().split(";");
		for (String entry : keyValues)
		{
			String[] keyValue = entry.split("=");
			if (key.equals(keyValue[0]))
			{
				return Double.valueOf(keyValue[1]);
			}
		}
		throw new RuntimeException("not found in map");
	}

	private long l(String value)
	{
		return Long.valueOf(value);
	}
}
