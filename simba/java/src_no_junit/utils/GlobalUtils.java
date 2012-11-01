package utils;

public class GlobalUtils
{
	public static boolean equals(double d1, double d2)
	{
		return Math.abs(d1 - d2) < 0.00000001d;
	}

	public static boolean equals(Object obj1, Object obj2)
	{
		if (null == obj1)
		{
			return (obj2 == null);
		}
		return obj1.equals(obj2);
	}
}
