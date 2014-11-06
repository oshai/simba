package sim;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
		verify(c).collect(host, job);
		verify(host).finishJob(job);
		assertEquals(1, jobFinisher.collectFinishedJobs());
		assertEquals(0, jobFinisher.collectFinishedJobs());
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
