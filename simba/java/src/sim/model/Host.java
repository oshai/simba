package sim.model;

import static com.google.common.collect.Lists.*;
import static com.intel.swiss.sws.mechanism.assertions.Asserter.*;

import java.util.List;

public class Host
{
	private String id;
	private double cores;
	private double memory;
	private List<Job> jobs = newArrayList();
	
	private Host(String id, double cores, double memory)
	{
		super();
		this.id = id;
		this.cores = cores;
		this.memory = memory;
	}
	
	@SuppressWarnings("hiding")
	public static class Builder
	{
		private String id;
		private double cores;
		private double memory;
		
		private Builder()
		{
			
		}
		
		public static Builder create()
		{
			return new Builder();
		}
		
		public Builder id(String id)
		{
			this.id = id;
			return this;
		}
		
		public Builder cores(double cores)
		{
			this.cores = cores;
			return this;
		}
		
		public Builder memory(double memory)
		{
			this.memory = memory;
			return this;
		}
		
		public Host build()
		{
			return new Host(id, cores, memory);
		}
	}
	
	public List<Job> jobs()
	{
		return jobs;
	}
	
	public void dispatchJob(Job job)
	{
		jobs.add(job);
	}
	
	public void finishJob(Job job)
	{
		asserter().assertTrue(jobs.remove(job));
	}
	
	public boolean hasAvailableResourcesFor(Job job)
	{
		return availableCores() >= job.cores() && availableMemory() >= job.memory();
	}
	
	public double availableMemory()
	{
		return memory - usedMemory();
	}
	
	public double usedMemory()
	{
		double $ = 0;
		for (Job job : jobs)
		{
			$ += job.memory();
		}
		return $;
	}
	
	public double availableCores()
	{
		return cores - usedCores();
	}
	
	public double usedCores()
	{
		double $ = 0;
		for (Job job : jobs)
		{
			$ += job.cores();
		}
		return $;
	}
	
	public double cores()
	{
		return cores;
	}
	
	public double memory()
	{
		return memory;
	}
	
	public String id()
	{
		return id;
	}
	
	@Override
	public String toString()
	{
		return "Host [id=" + id + ", cores=" + cores + ", memory=" + memory + ", jobs=" + jobs + "]";
	}
	
}
