package sim;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import utils.TextFileUtils;

public class CollisionDetection
{
	public static void main(String[] args) throws IOException
	{
		new CollisionDetection().execute();
	}

	private void execute() throws IOException
	{
		// int numOfJobs = 1;
		for (int i = 1; i < 20; i++)
		{
			System.out.println("test " + i);
			printForRetry(i);
		}
	}

	private void printForRetry(int retries) throws IOException
	{
		int numOfMachines = 14000;
		String result = "";
		for (int i = 1; i <= numOfMachines; i++)
		{
			int collisions = calcCollisions(i, numOfMachines, retries);
			result += i + " " + collisions + "\n";
		}
		String file = "collisions_" + retries;
		new File(file).createNewFile();
		TextFileUtils.setContents(file, result);
	}

	private int calcCollisions(int numOfJobs, int numOfMachines, int retries)
	{
		Set<Integer> machines = new HashSet<Integer>();
		Random random = new Random();
		int collisions = 0;
		for (int i = 0; i < numOfJobs; i++)
		{
			int machine = findMachine(numOfMachines, random, machines, retries);
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

	private int findMachine(int numOfMachines, Random random, Set<Integer> machines, int retries)
	{
		int machine = 0;
		for (int i = 0; i < retries; i++)
		{
			machine = random.nextInt(numOfMachines);
			if (!machines.contains(machine))
			{
				return machine;
			}
		}
		return machine;
	}
}
