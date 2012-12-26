package sim.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.collect.Lists;

public class ClusterTest
{

	@Test
	public void testAdd() throws Exception
	{
		Host h = Host.builder().build();
		assertEquals(Lists.newArrayList(h), new Cluster().add(h).hosts());
	}
}
