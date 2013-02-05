package sim.scheduling.graders;

import java.util.Random;

import sim.model.GradeableHost;
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
	public double getGrade(GradeableHost host, Job job)
	{
		return random.nextInt(limit);
	}

	@Override
	public String toString()
	{
		return "random";
	}

}
