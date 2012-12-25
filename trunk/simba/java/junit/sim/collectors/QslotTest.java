package sim.collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import sim.model.Job;

@RunWith(MockitoJUnitRunner.class)
public class QslotTest
{
	@Mock
	private QslotConfiguration conf;
	private Qslot qslot;

	@Before
	public void createQslot() throws Exception
	{
		qslot = new Qslot(conf);
	}

	@Test
	public void testShouldGetDefaultValueIsZero() throws Exception
	{
		assertEquals(0.0, qslot.absoluteShouldGet(), 0.0);
	}

	@Test
	public void testJobErrorWithNoJobsIsZero() throws Exception
	{
		assertEquals(0.0, qslot.jobError(5), 0.0);
	}

	@Test
	public void testEnumCoverage() throws Exception
	{
		EShouldGet.valueOf(EShouldGet.ABSOLUTE.name());
	}

	@Test
	public void testLongestWaitingJob() throws Exception
	{
		Job j = mock(Job.class);
		qslot.updateLongestWaitingJob(j);
		assertEquals(j, qslot.longestWaitingJob());
	}

	@Test
	public void testLongestJobShouldNotBeReplaced() throws Exception
	{
		Job j = mock(Job.class);
		when(j.submitTime()).thenReturn(1L);
		Job youngJob = mock(Job.class);
		when(youngJob.submitTime()).thenReturn(2L);
		qslot.updateLongestWaitingJob(j);
		qslot.updateLongestWaitingJob(youngJob);
		assertEquals(j, qslot.longestWaitingJob());
	}

	@Test
	public void testLongestJobShouldBeReplaced() throws Exception
	{
		Job j = mock(Job.class);
		when(j.submitTime()).thenReturn(2L);
		Job oldJob = mock(Job.class);
		when(oldJob.submitTime()).thenReturn(1L);
		qslot.updateLongestWaitingJob(j);
		qslot.updateLongestWaitingJob(oldJob);
		assertEquals(oldJob, qslot.longestWaitingJob());
	}

	@Test
	public void testShouldDisplayErrorWhenRelativeShouldGetError() throws Exception
	{
		Job j = mock(Job.class);
		when(j.submitTime()).thenReturn(2L);
		when(j.cost()).thenReturn(5.0);
		qslot.updateLongestWaitingJob(j);
		qslot.relativeShouldGet(0.5);
		qslot.hasWaitingJobs(true);
		assertEquals(0.5, qslot.relativeShouldGetError(), 0.0);
		assertEquals(0.1, qslot.jobError(3L), 0.0);
	}
}
