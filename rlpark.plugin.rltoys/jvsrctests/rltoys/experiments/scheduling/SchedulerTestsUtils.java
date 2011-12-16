package rltoys.experiments.scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.JobPool;
import rltoys.experiments.scheduling.interfaces.JobPool.JobPoolListener;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.internal.network.SocketClient;
import rltoys.experiments.scheduling.network.ServerScheduler;
import rltoys.experiments.scheduling.pools.FileJobPool;
import rltoys.experiments.scheduling.pools.PoolResults;
import rltoys.experiments.scheduling.schedulers.Schedulers;
import zephyr.plugin.core.api.signals.Listener;

public class SchedulerTestsUtils {
  static final String Localhost = "localhost";
  static final int Port = 5000;
  public static final int Timeout = 1000000;

  static class ClassResolutionListener implements Listener<String> {
    final List<String> names = new ArrayList<String>();

    @Override
    public void listen(String name) {
      names.add(name);
    }
  }

  static public class Job implements Runnable, Serializable {
    private static final long serialVersionUID = -1405281337225571229L;
    public boolean done = false;

    @Override
    public void run() {
      done = true;
    }
  }

  static public class JobDoneListener implements Listener<JobDoneEvent> {
    private final List<Runnable> done = new ArrayList<Runnable>();

    @Override
    public void listen(JobDoneEvent eventInfo) {
      done.add(eventInfo.done);
    }

    public int nbJobDone() {
      return done.size();
    }

    public List<Runnable> jobDone() {
      return done;
    }
  }

  static List<Job> createJobs(int nbJobs) {
    List<Job> jobs = new ArrayList<Job>();
    for (int i = 0; i < nbJobs; i++)
      jobs.add(new Job());
    return jobs;
  }

  static void assertAreDone(List<? extends Runnable> jobs, boolean isDone) {
    for (Runnable job : jobs)
      Assert.assertEquals(isDone, ((Job) job).done);
  }

  static public void testServerScheduler(ServerScheduler scheduler, int nbJobs) {
    if (scheduler.isLocalSchedulingEnabled()) {
      testScheduler(scheduler, nbJobs);
      return;
    }
    ClassResolutionListener listener = new ClassResolutionListener();
    SocketClient.onClassRequested.connect(listener);
    testScheduler(scheduler, nbJobs);
    SocketClient.onClassRequested.disconnect(listener);
    Assert.assertTrue(listener.names.contains(Job.class.getName()));
  }

  static public void testScheduler(Scheduler scheduler, int nbJobs) {
    List<Job> jobs = SchedulerTestsUtils.createJobs(nbJobs);
    SchedulerTestsUtils.assertAreDone(jobs, false);
    JobDoneListener listener = createListener();
    Schedulers.addAll(scheduler, jobs, listener);
    scheduler.runAll();
    Assert.assertEquals(nbJobs, listener.nbJobDone());
    SchedulerTestsUtils.assertAreDone(listener.jobDone(), true);
  }

  static public JobDoneListener createListener() {
    return new JobDoneListener();
  }

  static private JobPool[] createPools(List<Job> jobs, JobDoneListener jobListener, int nbPools,
      JobPoolListener poolListener) {
    JobPool[] pools = new FileJobPool[nbPools];
    for (int i = 0; i < pools.length; i++)
      pools[i] = new FileJobPool(poolListener, jobListener);
    for (int i = 0; i < jobs.size(); i++)
      pools[i % pools.length].add(jobs.get(i));
    return pools;
  }

  static public PoolResults submitJobsInPool(ServerScheduler scheduler, List<Job> jobs, JobDoneListener jobListener,
      JobPoolListener poolListener, int nbPool) {
    JobPool[] pools = createPools(jobs, jobListener, nbPool, poolListener);
    PoolResults poolResults = new PoolResults();
    for (JobPool pool : pools)
      poolResults.add(pool.submitTo(scheduler));
    return poolResults;
  }
}
