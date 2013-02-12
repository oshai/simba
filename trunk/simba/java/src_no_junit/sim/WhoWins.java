package sim;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		runOnCol();
	}

	private static void runOnCol()
	{
		Map<String, Pair<Integer, String>> whoWinsMemory = newHashMap();
		ArrayList<Integer> list = newArrayList(2, 3, 6, 7);

		String file = cmdArgs[0];
		String lines = TextFileUtils.getContents(new File(file));
		int sum = 0;
		int count = 0;
		int sumMf = 0;
		int sumBest = 0;
		for (String line : lines.split("\n"))
		{
			if (line.isEmpty())
			{
				continue;
			}
			try
			{
				List<String> split = Lists.newArrayList(Splitter.on(" ").omitEmptyStrings().split(line));
				int mf = (int) ((double) Double.valueOf(split.get(5)));
				int best = 0;
				for (Integer i : list)
				{
					int current = (int) ((double) Double.valueOf(split.get(i)));
					if (current > best)
					{
						best = current;
					}
				}
				int delta = best - mf;
				if (delta > 0)
				{
					// System.out.println("best - mf: " + delta);
					sum += delta;
					sumMf += mf;
					sumBest += best;
					count++;
				}
			}
			catch (NumberFormatException ex)
			{
				// ex.printStackTrace();
				System.out.println(ex.getMessage());
			}
		}
		System.out.println("average delta= " + (sum / count));
		System.out.println("average mf= " + (sumMf / count));
		System.out.println("average best= " + (sumBest / count));
	}

}
