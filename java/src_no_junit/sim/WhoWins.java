package sim;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.util.Pair;

import utils.TextFileUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class WhoWins
{

	private static String[] cmdArgs;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		WhoWins.cmdArgs = args;
		runOnCol(0);
	}

	private static void runOnCol(int col)
	{
		Map<String, Pair<Integer, String>> whoWinsMemory = newHashMap();
		ArrayList<Integer> list = newArrayList(2, 3, 4, 5, 6);
		for (Integer test : list)
		{
			calc(whoWinsMemory, test);
		}
		System.out.println("col " + col);
		for (Entry<String, Pair<Integer, String>> e : whoWinsMemory.entrySet())
		{
			System.out.println(e.getKey() + "=>" + e.getValue().getKey() + "," + e.getValue().getValue());
		}
	}

	private static void calc(Map<String, Pair<Integer, String>> whoWinsMemory, int col)
	{
		String test = "col:" + col;
		String file = cmdArgs[0];
		String lines = TextFileUtils.getContents(new File(file));
		for (String line : lines.split("\n"))
		{
			if (line.isEmpty())
			{
				continue;
			}
			try
			{
				List<String> split = Lists.newArrayList(Splitter.on(" ").omitEmptyStrings().split(line));

				int memory = (int) ((double) Double.valueOf(split.get(col)));
				if (memory > 0)// && Integer.valueOf(split[0]) > 1343671200)
				{
					String time = split.get(0);
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
