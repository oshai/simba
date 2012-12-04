package sim;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.util.Pair;

import utils.TextFileUtils;

public class WhoWins
{

	private static String[] cmdArgs;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		WhoWins.cmdArgs = args;
		runOnCol(Integer.valueOf(args[1]));
	}

	private static void runOnCol(int col)
	{
		Map<String, Pair<Integer, String>> whoWinsMemory = newHashMap();
		ArrayList<String> list = newArrayList("bf", "ff", "rf", "wf", "mf");
		for (String test : list)
		{
			calc(whoWinsMemory, test, col);
		}
		System.out.println("col " + col);
		for (Entry<String, Pair<Integer, String>> e : whoWinsMemory.entrySet())
		{
			System.out.println(e.getValue().getValue());
		}
	}

	private static void calc(Map<String, Pair<Integer, String>> whoWinsMemory, String test, int col)
	{
		String file = "/tmp/simba/iil_1_aug_traces_reservation_" + cmdArgs[0] + "xMemory_submit_buckets/" + test + "/machines_utilization";
		String lines = TextFileUtils.getContents(new File(file));
		for (String line : lines.split("\n"))
		{
			try
			{
				String[] split = line.split(" ");
				int memory = Integer.valueOf(split[col]);
				if (memory > 0 && Integer.valueOf(split[0]) > 1343671200)
				{
					if (whoWinsMemory.get(split[0]) == null || whoWinsMemory.get(split[0]).getKey() < memory)
					{
						whoWinsMemory.put(split[0], new Pair<Integer, String>(memory, test));
					}
				}
			}
			catch (NumberFormatException ex)
			{
				// ex.printStackTrace();
				System.out.println(ex.getMessage());
			}
		}
	}

}
