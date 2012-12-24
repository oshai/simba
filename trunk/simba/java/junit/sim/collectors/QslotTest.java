package sim.collectors;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
	public void testEnumCoverage() throws Exception
	{
		Qslot.SHOULD_GET.valueOf(Qslot.SHOULD_GET.ABSOLUTE.name());
	}
}
