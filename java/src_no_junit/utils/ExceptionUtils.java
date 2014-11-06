package utils;


public class ExceptionUtils
{
	
	public static RuntimeException asUnchecked(Throwable t)
	{
		if (t instanceof RuntimeException)
		{
			return (RuntimeException)t;
		}
		else if (t instanceof Error)
		{
			throw (Error)t;
		}
		else
		{
			return new RuntimeException("wrapped exception - check cause for details, message from cause:" + t.getMessage(), t);
		}
	}
	
}
