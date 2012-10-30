package utils.assertions;

public class AsserterStrategyThrowsError extends AbstractAsserterStrategy
{

	@Override
	public void fail(boolean actual, Object... message)
	{
		throw new AssertionError(formatMessageBoolean(actual, message));
	}

	@Override
	public void fail(Object... message)
	{
		throw new AssertionError(formatMessage(message));
	}
	
}
