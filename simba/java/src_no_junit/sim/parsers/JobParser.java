package sim.parsers;

import javax.inject.Provider;

import org.apache.log4j.Logger;

import sim.Clock;
import sim.event_handling.EventQueue;
import sim.events.Submit;
import sim.model.Cluster;
import sim.model.Host;
import sim.model.Job;

import com.google.common.base.Predicate;
import com.intel.swiss.sws.mechanism.file.TextFileUtil;

public class JobParser
{
	private static final Logger log = Logger.getLogger(JobParser.class);
	private static final String JOBS_FILE = "/nfs/iil/iec/sws/work/oshai/public/workload/traces2/iil1_trace_14-10-28-10-2012.csv";
	
	public EventQueue parse(Provider<Clock> clockProvider, final Cluster cluster)
	{
		final EventQueue $ = new EventQueue(clockProvider);
		Predicate<String> predicate = new Predicate<String>()
		{
			@Override
			public boolean apply(String line)
			{
				try
				{
					String[] cols = line.split(",");
					double cores = getMapValue("cores", cols[20]);
					double memory = getMapValue("memory", cols[20]);
					long length = l(cols[6]) - l(cols[5]);
					if (length < 1)
					{
						length = 10;
					}
					Job job = Job.Builder.create(length).id(cols[0]).priority(l(cols[5])).submitTime(l(cols[4])).cores(cores).memory(memory).build();
					if (canRun(job, cluster))
					{
						$.add(new Submit(job));
					}
					else
					{
						log.warn("apply() - job cannot run " + job.cores() + " " + job.memory());
					}
				}
				catch (Exception ex)
				{
					log.warn("apply() - error: " + ex.getMessage() + "; on line " + line);
				}
				return true;
			}
			
		};
		TextFileUtil.getContentByLines(JOBS_FILE, predicate);
		return $;
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
