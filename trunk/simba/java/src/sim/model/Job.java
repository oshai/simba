package sim.model;

public class Job
{
	private static final long MAX_JOB_LENGTH = 60 * 60 * 24 * 31;// 1 month

	private final String id;
	private final long priority;
	private final long submitTime;
	private final long length;
	private final double cores;
	private final double memory;
	private long startTime;
	private final double cost;
	private final String qslot;

	private Job(Builder builder)
	{
		super();
		this.id = builder.id;
		this.priority = builder.priority;
		this.submitTime = builder.submitTime;
		this.length = builder.length;
		this.cores = builder.cores;
		this.memory = builder.memory;
		this.startTime = builder.startTime;
		this.cost = builder.cost;
		this.qslot = builder.qslot;
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

	@SuppressWarnings("hiding")
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
		private String qslot;

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
			return new Job(this);
		}

		public Builder cost(double cost)
		{
			this.cost = cost;
			return this;
		}

		public Builder qslot(String qslot)
		{
			this.qslot = qslot;
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

	public String qslot()
	{
		return qslot;
	}

	@Override
	public String toString()
	{
		return "Job [id=" + id + ", priority=" + priority + ", submitTime=" + submitTime + ", length=" + length + ", cores=" + cores + ", memory=" + memory + ", startTime=" + startTime + ", cost=" + cost + ", qslot=" + qslot + "]";
	}

}
