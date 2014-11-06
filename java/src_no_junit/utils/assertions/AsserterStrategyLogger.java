package utils.assertions;

import org.apache.log4j.Logger;


class AsserterStrategyLogger extends AbstractAsserterStrategy
{
	private static final Logger log = Logger.getLogger(AsserterStrategyLogger.class);
	private final Logger m_logger;
	private final boolean m_withStacktrace;

	public AsserterStrategyLogger(Logger logger, boolean withStacktrace)
	{
		m_logger = logger;
		m_withStacktrace = withStacktrace;
	}

	public AsserterStrategyLogger()
	{
		this(log, defalutWithStacktrace());
	}


	public AsserterStrategyLogger(boolean withStacktrace, IAsserterStrategy strategy)
	{
		this(getLoggreFromStrategy(strategy), withStacktrace);
	}

	public AsserterStrategyLogger(Logger logger, IAsserterStrategy strategy)
	{
		this(logger, getWithStacktraceFromStrategy(strategy));
	}

	private static Logger getLoggreFromStrategy(IAsserterStrategy strategy)
	{
		if (strategy instanceof AsserterStrategyLogger)
		{
			return ((AsserterStrategyLogger)strategy).m_logger;
		}
		return log;
	}
	private static boolean getWithStacktraceFromStrategy(IAsserterStrategy strategy)
	{
		if (strategy instanceof AsserterStrategyLogger)
		{
			return ((AsserterStrategyLogger)strategy).m_withStacktrace;
		}
		return defalutWithStacktrace();
	}

	private static boolean defalutWithStacktrace()
	{
		return true;
	}
	@Override
	public void fail(Object... message)
	{
		String formatMessage = formatMessage(message);
		failFormattedMessage(formatMessage);
	}

	private void failFormattedMessage(String formatMessage)
	{
		if (m_withStacktrace)
		{
			AssertionError e = new AssertionError(formatMessage);
			m_logger.error(formatMessage, e);
			e.printStackTrace();
		}
		else
		{
			m_logger.error(formatMessage);
			System.err.println(formatMessage);
		}
	}

	@Override
	public void fail(boolean actual, Object... message)
	{
		String formatMessage = formatMessageBoolean(actual, message);
		failFormattedMessage(formatMessage);
	}
	
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_logger == null) ? 0 : m_logger.hashCode());
		result = prime * result + (m_withStacktrace ? 1231 : 1237);
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
		AsserterStrategyLogger other = (AsserterStrategyLogger)obj;
		if (m_logger == null)
		{
			if (other.m_logger != null)
				return false;
		}
		else if (!m_logger.equals(other.m_logger))
			return false;
		if (m_withStacktrace != other.m_withStacktrace)
			return false;
		return true;
	}
	
	
}