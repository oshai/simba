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
		runOnCol(Integer.valueOf(args[0]));
	}

	private static void runOnCol(int col)
	{
		Map<String, Pair<Integer, String>> whoWinsMemory = newHashMap();
		ArrayList<String> list = newArrayList("best-fit", "mix-fit", "worse-fit", "worse-fit-cores", "best-fit-cores");
		for (String test : list)
		{
			calc(whoWinsMemory, test, col);
		}
		System.out.println("col " + col);
		for (Entry<String, Pair<Integer, String>> e : whoWinsMemory.entrySet())
		{
			System.out.println(e.getKey() + "=>" + e.getValue().getKey() + "," + e.getValue().getValue());
		}
	}

	private static void calc(Map<String, Pair<Integer, String>> whoWinsMemory, String test, int col)
	{
		String file = cmdArgs[1] + "/" + test + "/machines_utilization";
		String lines = TextFileUtils.getContents(new File(file));
		for (String line : lines.split("\n"))
		{
			try
			{
				String[] split = line.split(" ");
				int memory = Integer.valueOf(split[col]);
				if (memory > 0)// && Integer.valueOf(split[0]) > 1343671200)
				{
					String time = split[0];
					if (whoWinsMemory.get(time) == null || whoWinsMemory.get(time).getKey() < memory)
					{
						whoWinsMemory.put(time, new Pair<Integer, String>(memory, test));
					}
					else if (memory == whoWinsMemory.get(time).getKey())
					{
						whoWinsMemory.put(time, new Pair<Integer, String>(memory, whoWinsMemory.get(time).getValue() + " " + test));
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
