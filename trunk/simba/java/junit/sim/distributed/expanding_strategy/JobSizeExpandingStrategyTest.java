package sim.distributed.expanding_strategy;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import sim.DistributedSimbaConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class JobSizeExpandingStrategyTest
{
	@Mock
	private DistributedSimbaConfiguration simbaConfiguration;
	private JobSizeExpandingStrategy jobSizeExpandingStrategy;

	@Before
	public void createJobSizeExpandingStrategy() throws Exception
	{
		jobSizeExpandingStrategy = new JobSizeExpandingStrategy(simbaConfiguration);
	}

	@Test
	public void testMemoryDividedToInitialDisatchFactory() throws Exception
	{
		when(simbaConfiguration.intialDispatchFactor()).thenReturn(10);
		assertEquals(10, jobSizeExpandingStrategy.times(1, 99));
		assertEquals(2, jobSizeExpandingStrategy.times(1, 10));
	}

	@Test
	public void testShouldBeMinimum1() throws Exception
	{
		when(simbaConfiguration.intialDispatchFactor()).thenReturn(10);
		assertEquals(1, jobSizeExpandingStrategy.times(1, 8));
	}
}
