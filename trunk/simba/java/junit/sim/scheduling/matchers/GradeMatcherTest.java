package sim.scheduling.matchers;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.Mockito;

import sim.model.Host;
import sim.model.Job;
import sim.scheduling.graders.Grader;

import com.google.common.collect.Lists;

public class GradeMatcherTest
{

	@Test
	public void testNoResult()
	{
		Job job = Mockito.mock(Job.class);
		Host host1 = mock(Host.class);
		when(host1.hasAvailableResourcesFor(job)).thenReturn(true);
		Host host2 = mock(Host.class);
		when(host2.hasAvailableResourcesFor(job)).thenReturn(true);
		Grader grader = mock(Grader.class);
		when(grader.getGrade(host1, job)).thenReturn(1.0);
		when(grader.getGrade(host2, job)).thenReturn(1.0);
		assertEquals(host1, new GradeMatcher(newArrayList(grader)).match(job, newArrayList(host1, host2)));
	}

	@Test
	public void testToString()
	{
		new GradeMatcher(Lists.<Grader> newArrayList()).toString();
	}

	@Test
	public void testHost1GradingWins()
	{
		Job job = Mockito.mock(Job.class);
		Host host1 = mock(Host.class);
		when(host1.hasAvailableResourcesFor(job)).thenReturn(true);
		Host host2 = mock(Host.class);
		when(host2.hasAvailableResourcesFor(job)).thenReturn(true);
		Grader grader = mock(Grader.class);
		when(grader.getGrade(host1, job)).thenReturn(2.0);
		when(grader.getGrade(host2, job)).thenReturn(1.0);
		assertEquals(host1, new GradeMatcher(grader).match(job, newArrayList(host1, host2)));
	}

	@Test
	public void testHost2GradingWins()
	{
		Job job = Mockito.mock(Job.class);
		Host host1 = mock(Host.class);
		when(host1.hasAvailableResourcesFor(job)).thenReturn(true);
		Host host2 = mock(Host.class);
		when(host2.hasAvailableResourcesFor(job)).thenReturn(true);
		Grader grader = mock(Grader.class);
		when(grader.getGrade(host1, job)).thenReturn(1.0);
		when(grader.getGrade(host2, job)).thenReturn(2.0);
		assertEquals(host2, new GradeMatcher(newArrayList(grader)).match(job, newArrayList(host1, host2)));
	}

}
