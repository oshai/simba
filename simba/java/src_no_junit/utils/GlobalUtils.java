package utils;

public class GlobalUtils
{
	public static boolean equals(double d1, double d2)
	{
		return Double.compare(d1, d2) == 0;
	}

	public static boolean equals(Object obj1, Object obj2)
	{
		if (null == obj1)
		{
			return (obj2 == null);
		}
		return obj1.equals(obj2);
	}

	public static boolean greater(double d1, double d2)
	{
		return d1 > d2;
	}

	public static boolean greaterOrEquals(double d1, double d2)
	{
		if (equals(d1, d2))
		{
			return true;
		}
		return d1 > d2;
	}
}
