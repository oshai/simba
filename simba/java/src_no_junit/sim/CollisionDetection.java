package sim;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import utils.TextFileUtils;

public class CollisionDetection
{
	public static void main(String[] args)
	{
		new CollisionDetection().execute();
	}

	private void execute()
	{
		// int numOfJobs = 1;
		int numOfMachines = 14000;
		String result = "";
		for (int i = 1; i <= numOfMachines; i++)
		{
			int collisions = calcCollisions(i, numOfMachines);
			result += i + " " + collisions + "\n";

		}
		TextFileUtils.setContents("collisions", result);
	}

	private int calcCollisions(int numOfJobs, int numOfMachines)
	{
		Set<Integer> machines = new HashSet<Integer>();
		Random random = new Random();
		int collisions = 0;
		for (int i = 0; i < numOfJobs; i++)
		{
			int machine = random.nextInt(numOfMachines);
			if (machines.contains(machine))
			{
				collisions++;
			}
			else
			{
				machines.add(machine);
			}

		}
		return collisions;
	}
}
