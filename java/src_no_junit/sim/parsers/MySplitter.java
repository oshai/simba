package sim.parsers;

import static com.google.common.collect.Lists.*;

import java.util.List;

import com.google.common.base.Splitter;

public class MySplitter
{
	private static Splitter splitterComma = Splitter.on(",");
	private static Splitter splitterEquals = Splitter.on("=");
	private static Splitter splitterSemicolon = Splitter.on(";");
	private static Splitter splitterSlash = Splitter.on("/");

	public static List<String> splitComma(String value)
	{
		List<String> $ = newArrayList();
		for (String string : splitterComma.split(value))
		{
			$.add(string);
		}
		return $;
	}

	public static Iterable<String> splitEquals(String value)
	{
		return splitterEquals.split(value);
	}

	public static Iterable<String> splitSlash(String value)
	{
		return splitterSlash.split(value);
	}

	public static Iterable<String> splitSemicolon(String value)
	{
		return splitterSemicolon.split(value);
	}
}
