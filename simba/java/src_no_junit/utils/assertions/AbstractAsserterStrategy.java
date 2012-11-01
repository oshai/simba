package utils.assertions;

import utils.to;

public abstract class AbstractAsserterStrategy implements IAsserterStrategy
{
	
	public AbstractAsserterStrategy()
	{
		super();
	}
	
	protected final String formatMessage(Object... message)
	{
		return to.string(message, " ");
	}
	
	protected final String formatMessageBoolean(boolean actual, Object... message)
	{
		return "assertion failed: expected " + !actual + " but was " + actual + "; " + formatMessage(message);
	}
	
}
