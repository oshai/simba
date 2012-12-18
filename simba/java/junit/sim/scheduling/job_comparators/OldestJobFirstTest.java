package sim.scheduling.job_comparators;

import static junit.framework.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import sim.model.Job;

import com.google.common.collect.Lists;

public class OldestJobFirstTest
{
	@Test
	public void testCompare() throws Exception
	{
		List<Job> l = Lists.newArrayList(Job.builder(1).submitTime(2).build(), Job.builder(1).submitTime(1).build());
		ArrayList<Job> sorted = Lists.newArrayList(l);
		ArrayList<Job> expected = Lists.newArrayList(l);
		Collections.reverse(expected);
		Collections.sort(sorted, new OldestJobFirst());
		assertEquals(expected, sorted);

	}
}
