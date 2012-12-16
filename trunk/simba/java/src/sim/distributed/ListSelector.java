package sim.distributed;

import java.util.List;

interface ListSelector
{

	<T> T selectFromList(List<T> list);

}