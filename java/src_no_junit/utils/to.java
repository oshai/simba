package utils;

public class to
{

	public static String string(Object[] array, String delimiter)
	{
		if (null == array)
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int i = 0; i < array.length; i++)
		{
			if (!first)
			{
				sb.append(delimiter);
				first = false;
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}
	
}