package sim.scheduling.job_comparators;

import static org.mockito.Mockito.*;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import sim.collectors.CostStatistics;
import sim.collectors.Qslot;
import sim.model.Job;

import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class FairShareComparatorTest
{
	@Mock
	private CostStatistics stats;
	private FairShareComparator tested;

	@Before
	public void createSingleHostFairShareComparator() throws Exception
	{
		tested = new FairShareComparator(stats);
	}

	@Test
	public void testCompare()
	{
		Map<String, Qslot> m = Maps.newHashMap();
		Job j1 = createJob(m, "qslot1", 5.0);
		Job j2 = createJob(m, "qslot2", 3.0);
		when(stats.qslots()).thenReturn(m);
		Assert.assertEquals(1, tested.compare(j1, j2));
		Assert.assertEquals(-1, tested.compare(j2, j1));
	}

	private Job createJob(Map<String, Qslot> m, String qslotName, double shouldGetDelta)
	{
		Job j1 = mock(Job.class);
		when(j1.qslot()).thenReturn(qslotName);
		Qslot qslot = mock(Qslot.class);
		when(qslot.name()).thenReturn(qslotName);
		when(qslot.relativeShouldGetDelta()).thenReturn(shouldGetDelta);
		m.put(j1.qslot(), qslot);
		return j1;
	}
}
