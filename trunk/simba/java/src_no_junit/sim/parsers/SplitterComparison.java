package sim.parsers;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;

public class SplitterComparison
{

	private static final double CONST = 1e6;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		for (int i = 0; i < CONST; i++)
		{
			test();
		}
	}

	private static void test()
	{
		long st = System.currentTimeMillis();
		for (int i = 0; i < CONST; i++)
		{
			StringTokenizer t = new StringTokenizer("dog,,cat", ",");
			while (t.hasMoreTokens())
			{
				t.nextToken();
			}
		}
		System.out.println("StringTokenizer\t" + time(st));

		st = System.currentTimeMillis();
		Splitter splitter = Splitter.on(",");
		for (int i = 0; i < CONST; i++)
		{
			Iterable<String> mt = splitter.split("dog,,cat");
			for (String t : mt)
			{
			}
		}
		System.out.println("Google Guava Splitter\t" + time(st));

		st = System.currentTimeMillis();
		for (int i = 0; i < CONST; i++)
		{
			String[] tokens = "dog,,cat".split(",");
			for (String t : tokens)
			{
			}
		}
		System.out.println("String.split\t" + time(st));

		st = System.currentTimeMillis();
		Pattern p = Pattern.compile(",");
		for (int i = 0; i < CONST; i++)
		{
			String[] tokens = p.split("dog,,cat");
			for (String t : tokens)
			{
			}
		}
		System.out.println("regexp\t" + time(st));
	}

	private static long time(long st)
	{
		return System.currentTimeMillis() - st;
	}

}
