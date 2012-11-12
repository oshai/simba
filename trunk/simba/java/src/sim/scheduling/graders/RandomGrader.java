package sim.scheduling.graders;

import java.util.Random;

import sim.model.Host;
import sim.model.Job;

public class RandomGrader implements Grader
{
	private Random random = new Random();
	private int limit;

	public RandomGrader(int limit)
	{
		this.limit = limit;
	}

	@Override
	public double getGrade(Host host, Job job)
	{
		return random.nextInt(limit);
	}

}
