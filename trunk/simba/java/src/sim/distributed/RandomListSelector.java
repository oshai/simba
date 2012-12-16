package sim.distributed;

import java.util.List;
import java.util.Random;

public class RandomListSelector implements ListSelector
{

	private Random random = new Random();

	@Override
	public <T> T selectFromList(List<T> list)
	{
		if (list.isEmpty())
		{
			return null;
		}
		return list.get(random.nextInt(list.size()));
	}

}
