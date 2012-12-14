package sim.model;

public class Job
{
	private static final long MAX_JOB_LENGTH = 60 * 60 * 24 * 31;// 1 month

	private String id;
	private long priority;
	private long submitTime;
	private long length;
	private double cores;
	private double memory;
	private long startTime;
	private double cost;

	private Job(String id, long priority, long submitTime, long length, double cores, double memory, long startTime, double cost)
	{
		super();
		this.id = id;
		this.priority = priority;
		this.submitTime = submitTime;
		this.length = length;
		this.cores = cores;
		this.memory = memory;
		this.startTime = startTime;
		this.cost = cost;
		validate();
	}

	private void validate()
	{
		if (length < 1)
		{
			throw new IllegalArgumentException(id + " length < 1 : " + length);
		}
		if (length > MAX_JOB_LENGTH)
		{
			throw new IllegalArgumentException(id + " length > + " + MAX_JOB_LENGTH + " : " + length);
		}
	}

	public static void main(String[] args)
	{
		System.out.println();
	}

	public static class Builder
	{
		private String id;
		private long priority;
		private long submitTime;
		private long length;
		private double cores;
		private double cost;
		private double memory;
		private long startTime;

		private Builder(long length)
		{
			this.length = length;
		}

		public Builder id(String id)
		{
			this.id = id;
			return this;
		}

		public Builder priority(long priority)
		{
			this.priority = priority;
			return this;
		}

		public Builder submitTime(long submitTime)
		{
			this.submitTime = submitTime;
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

		public Builder startTime(long startTime)
		{
			this.startTime = startTime;
			return this;
		}

		public Job build()
		{
			return new Job(id, priority, submitTime, length, cores, memory, startTime, cost);
		}

		public Builder cost(double cost)
		{
			this.cost = cost;
			return this;
		}
	}

	public long submitTime()
	{
		return submitTime;
	}

	public static Builder builder(long length)
	{
		return new Builder(length);
	}

	public long priority()
	{
		return priority;
	}

	public long length()
	{
		return length;
	}

	public double cores()
	{
		return cores;
	}

	public double memory()
	{
		return memory;
	}

	public long waitTime()
	{
		return startTime - submitTime;
	}

	public void started(long time)
	{
		startTime = time;
	}

	public String id()
	{
		return id;
	}

	public long startTime()
	{
		return startTime;
	}

	public double cost()
	{
		return cost;
	}

	@Override
	public String toString()
	{
		return "Job [id=" + id + ", priority=" + priority + ", submitTime=" + submitTime + ", length=" + length + ", cores=" + cores + ", memory=" + memory + ", startTime=" + startTime + ", cost=" + cost + "]";
	}

}
