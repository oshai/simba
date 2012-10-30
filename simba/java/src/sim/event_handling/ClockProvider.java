package sim.event_handling;

import javax.inject.Provider;

import sim.Clock;

public class ClockProvider implements Provider<Clock>
{

	private final Clock clock;

	public ClockProvider(Clock clock)
	{
		super();
		this.clock = clock;
	}

	@Override
	public Clock get()
	{
		return clock;
	}
	
}
