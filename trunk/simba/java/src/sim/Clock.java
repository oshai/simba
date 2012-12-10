package sim;

public class Clock
{

	private long time;

	public Clock()
	{
		this(0);
	}

	public Clock(long time)
	{
		this.time = time;
	}

	public long tick()
	{
		time++;
		return time;
	}

	public long time()
	{
		return time;
	}

	public void time(long newtime)
	{
		this.time = newtime;
	}

}