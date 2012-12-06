package sim.model;

import static com.google.common.collect.Lists.*;
import static utils.GlobalUtils.*;
import static utils.assertions.Asserter.*;

import java.util.Collections;
import java.util.List;

public class Host
{
	private String id;
	private double cores;
	private double memory;
	private double usedCores;
	private double usedMemory;
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
		return Collections.unmodifiableList(jobs);
	}

	public static Builder create()
	{
		return new Builder();
	}

	public void dispatchJob(Job job)
	{
		jobs.add(job);
		usedCores += job.cores();
		usedMemory += job.memory();
	}

	public void finishJob(Job job)
	{
		asserter().assertTrue(jobs.remove(job));
		usedCores -= job.cores();
		usedMemory -= job.memory();
	}

	public boolean hasAvailableResourcesFor(Job job)
	{
		return greaterOrEquals(availableCores(), job.cores()) && greaterOrEquals(availableMemory(), job.memory());
	}

	public double availableMemory()
	{
		return memory - usedMemory();
	}

	public double usedMemory()
	{
		return usedMemory;
	}

	public double availableCores()
	{
		return cores - usedCores();
	}

	public double usedCores()
	{
		return usedCores;
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

	public boolean hasPotentialResourceFor(Job job)
	{
		return greaterOrEquals(cores(), job.cores()) && greaterOrEquals(memory(), job.memory());
	}

}
