package utils.assertions;

import org.apache.log4j.Logger;

import utils.GlobalUtils;

public class Asserter
{
	public static boolean enabled = true;
	private final IAsserterStrategy m_strategy;

	public Asserter(IAsserterStrategy strategy)
	{
		m_strategy = strategy;
	}

	public static Asserter asserter()
	{
		return doo();
	}

	public static Asserter doo()
	{
		return new Asserter(new AsserterStrategyLogger());
	}

	public Asserter throwsError()
	{
		return new Asserter(new AsserterStrategyThrowsError());
	}

	public boolean assertTrue(boolean condition, String message)
	{
		return assertEquals(true, condition, message);
	}

	public boolean assertEquals(Object expected, Object actual, String message)
	{
		if (GlobalUtils.equals(expected, actual))
		{
			return true;
		}
		return fail(actual, message);
	}

	private boolean fail(Object actual, String message)
	{
		m_strategy.fail(actual, message);
		return false;
	}

	/**
	 * @param condition
	 * @return true if condition is false
	 */
	public boolean assertFalse(boolean condition, String message)
	{
		return assertEquals(false, condition, message);
	}

	/**
	 * @param condition
	 * @return true if condition is false
	 */
	public boolean assertFalse(boolean condition)
	{
		return assertFalse(condition, " expected false");
	}

	public boolean assertTrue(boolean condition)
	{
		return assertTrue(condition, " expected true");
	}

	public boolean fail(String message)
	{
		m_strategy.fail(message);
		return false;
	}

	public Asserter onLogger(Logger logger)
	{
		return new Asserter(new AsserterStrategyLogger(logger, m_strategy));
	}

	public Asserter withoutStacktrace()
	{
		return new Asserter(new AsserterStrategyLogger(false, m_strategy));
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_strategy == null) ? 0 : m_strategy.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Asserter other = (Asserter) obj;
		if (m_strategy == null)
		{
			if (other.m_strategy != null)
				return false;
		}
		else if (!m_strategy.equals(other.m_strategy))
			return false;
		return true;
	}

	public boolean assertNotNull(Object obj)
	{
		return assertNotNull(obj, " object is null");
	}

	public boolean assertNotNull(Object obj, String message)
	{
		if (null != obj)
		{
			return true;
		}
		return fail(false, message);
	}

}
