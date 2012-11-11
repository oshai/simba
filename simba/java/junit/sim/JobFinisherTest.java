package sim;

import static org.mockito.Mockito.*;

import org.apache.commons.math3.util.Pair;
import org.junit.Test;

import sim.collectors.JobCollector;
import sim.events.Finish;
import sim.model.Host;
import sim.model.Job;

public class JobFinisherTest
{

	@Test
	public void testFinish()
	{
		JobCollector c = mock(JobCollector.class);
		JobFinisher jobFinisher = new JobFinisher(c);
		Job job = mock(Job.class);
		Host host = mock(Host.class);
		jobFinisher.finish(new Finish(5, job, host));
		verify(c).collect(new Pair<Job, Host>(job, host));
		verify(host).finishJob(job);
	}

	@Test
	public void testFinishExecution()
	{
		JobCollector c = mock(JobCollector.class);
		JobFinisher jobFinisher = new JobFinisher(c);
		jobFinisher.finishExecution();
		verify(c).finish();
	}

}
