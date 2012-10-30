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
	
	public static String arrayToString(Object[] arrValues, String sParamDelimiter)
	{
		if (null == arrValues)
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int i = 0; i < arrValues.length; i++)
		{
			if (!first)
			{
				sb.append(sParamDelimiter);
				first = false;
			}
			sb.append(arrValues[i]);
		}
		return sb.toString();
	}
}
