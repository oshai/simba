package utils;

import java.io.File;
import java.io.IOException;

public class FileUtils
{
	public static void createNewFile(String filename)
	{
		File file = new File(filename);
		if (file.exists())
		{
			file.delete();
		}
		try
		{
			file.createNewFile();
		}
		catch (IOException ex)
		{
			throw ExceptionUtils.asUnchecked(ex);
		}
	}
}
