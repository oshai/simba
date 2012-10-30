package sim.collectors;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import utils.ExceptionUtils;
import utils.FileUtils;
import utils.TextFileUtils;

public abstract class Collector<T>
{
	protected static final String SEPERATOR = " ";
	private Writer writer;
	
	public Collector()
	{
		super();
		init();
	}
	
	protected abstract String collectHeader();
	
	protected abstract String collectLine(T time);
	
	public void collect(T t)
	{
		if (shouldAppend(t))
		{
			appendLine(collectLine(t));
		}
	}
	
	protected boolean shouldAppend(T t)
	{
		return true;
	}
	
	private void appendLine(String line)
	{
		try
		{
			writer.append(line + "\n");
		}
		catch (IOException ex)
		{
			throw ExceptionUtils.asUnchecked(ex);
		}
	}
	
	private void init()
	{
		FileUtils.createNewFile(getFileName());
		writer = TextFileUtils.getWriter(new File(getFileName()), true);
		String line = collectHeader();
		appendLine(line);
	}
	
	protected abstract String getFileName();
	
	public void finish()
	{
		try
		{
			writer.close();
		}
		catch (IOException ex)
		{
			throw ExceptionUtils.asUnchecked(ex);
		}
	}
	
}
