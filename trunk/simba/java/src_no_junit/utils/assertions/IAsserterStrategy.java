package utils.assertions;

public interface IAsserterStrategy
{

	void fail(boolean actual, Object... message);
	void fail(Object... message);
	
}
