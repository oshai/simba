package sim.distributed;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import sim.DistributedSimbaConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class ConstantExpandingStrategyTest
{
	@Mock
	private DistributedSimbaConfiguration simbaConfiguration;

	@InjectMocks
	private ConstantExpandingStrategy addExpandingStrategy;

	@Test
	public void testTimes()
	{
		int value = 3;
		when(simbaConfiguration.intialDispatchFactor()).thenReturn(value);
		assertEquals(value, addExpandingStrategy.times(2));
	}

}
