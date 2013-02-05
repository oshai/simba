package sim.scheduling.reserving;

import java.util.Map;

import sim.model.Host;
import sim.model.Job;

public class ScheduleCostResult
{

	public final String algorithmName;
	public final double cost;
	public final Map<Job, Host> shceduledJobsToHost;

	public ScheduleCostResult(String algorithmName, double cost, Map<Job, Host> shceduledJobsToHost)
	{
		this.algorithmName = algorithmName;
		this.cost = cost;
		this.shceduledJobsToHost = shceduledJobsToHost;
	}

	@Override
	public String toString()
	{
		return "ScheduleCostResult [algorithmName=" + algorithmName + ", cost=" + cost + "]";
	}

}
