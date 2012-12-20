package sim.collectors;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import utils.ExceptionUtils;
import utils.FileUtils;
import utils.TextFileUtils;

public abstract class Collector
{
	protected static final String SEPERATOR = " ";
	private Writer writer;

	public Collector()
	{
		super();
	}

	protected abstract String collectHeader();

	protected void appendLine(String line)
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

	public void init()
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
