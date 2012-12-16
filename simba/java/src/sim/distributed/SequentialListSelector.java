package sim.distributed;

import java.util.List;

class SequentialListSelector implements ListSelector
{
	@Override
	public <T> T selectFromList(List<T> list)
	{
		if (list.isEmpty())
		{
			return null;
		}
		return list.remove(0);
	}
}