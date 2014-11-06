package sim.distributed;

import static com.google.common.collect.Lists.*;
import static junit.framework.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

public class RandomListSelectorTest
{

	@Test
	public void testSelectFromList()
	{
		RandomListSelector tested = new RandomListSelector();
		List<String> l = newArrayList("A", "B", "C");
		HashSet<String> expected = Sets.newHashSet(l);
		Set<String> actual = Sets.newHashSet();
		actual.add(tested.selectFromList(l));
		actual.add(tested.selectFromList(l));
		actual.add(tested.selectFromList(l));
		assertEquals(expected, actual);
		assertNull(tested.selectFromList(l));
	}
}
