package sim.collectors;

public class SumAllocations
{
	public double sumAllocations;
	public double sumRunningAllocations;
	public double sumWaitingAllocations;
	public double sumWaitingRunningAllocations;

	public void update(Qslot value)
	{
		sumAllocations += value.allocation();
		if (value.hasWaitingJobs() || value.hasRunningJobs())
		{
			sumWaitingRunningAllocations += value.allocation();
		}
		if (value.hasRunningJobs())
		{
			sumRunningAllocations += value.allocation();
		}
		if (value.hasWaitingJobs())
		{
			sumWaitingAllocations += value.allocation();
		}
	}
}