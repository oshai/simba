package sim.scheduling.out_of_oreder;


public class MaxJobsOutOfOrderScheduler // implements Scheduler
{

	// private ReservingScheduler whatsLeftScheduler;
	// private/* final */JobDispatcher dispatcher;
	// private/* final */int scheduledJobs;
	//
	// @Override
	// public int schedule(long time)
	// {
	// // filter hosts that can accept jobs
	// // waiting queue: long jobs, small/balanced/big jobs, all jobs
	//
	// // next step 2: Go over long waiting jobs (more than x hours) and
	// // schedule them – make reservations to prevent starvation.
	// // return job->host
	// // next step: Go over hosts – and pack them with most fit jobs by the
	// // mix.
	// // update job->host
	// // Schedule what’s left – go over hosts and find most job for them
	// // (maybe use reserving scheduler?)
	//
	// Map<Job, Host> schedule = newHashMap();
	// schedule = whatsLeftScheduler.scheduleWithoutDispatch(time);
	// dispatch(schedule, time);
	// return scheduledJobs;
	// }
	//
	// private void dispatch(Map<Job, Host> dispatchedJobs, long time)
	// {
	// // for (Entry<Job, Host> entry : dispatchedJobs.entrySet())
	// // {
	// // if (!DUMMY_HOST.equals(e.))
	// // }
	// // Iterator<Job> iterator = waitingQueue.iterator();
	// // int processedJobsCount2 = 0;
	// // while (iterator.hasNext() && processedJobsCount2 <
	// // simbaConfiguration.jobsCheckedBySchduler())
	// // {
	// // processedJobsCount2++;
	// // Job job = iterator.next();
	// // Host host = dispatchedJobs.get(job);
	// // if (null != host && !DUMMY_HOST.equals(host))
	// // {
	// // scheduledJobs++;
	// // dispatcher.dispatch(job, host, time);
	// // iterator.remove();
	// // }
	// // }
	// }
}
