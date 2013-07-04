package rlpark.plugin.rltoys.junit.experiments.scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobPool;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobPool.JobPoolListener;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.Scheduler;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.TimedJob;
import rlpark.plugin.rltoys.experiments.scheduling.internal.network.SocketClient;
import rlpark.plugin.rltoys.experiments.scheduling.network.ServerScheduler;
import rlpark.plugin.rltoys.experiments.scheduling.pools.FileJobPool;
import rlpark.plugin.rltoys.experiments.scheduling.pools.PoolResults;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.Schedulers;
import zephyr.plugin.core.api.signals.Listener;

public class SchedulerTestsUtils {
  static final String Localhost = "localhost";
  public static final int Port = 5000;
  public static final int Timeout = 1000000;

  static class ClassResolutionListener implements Listener<String> {
    final List<String> names = new ArrayList<String>();

    @Override
    public void listen(String name) {
      names.add(name);
    }
  }

  static public class Job implements TimedJob, Runnable, Serializable {
    private static final long serialVersionUID = -1405281337225571229L;
    public boolean done = false;

    @Override
    public void run() {
      done = true;
    }

    @Override
    public long getComputationTimeMillis() {
      return 1;
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

    public boolean checkJobs(int nbJobs) {
      if (nbJobs != nbJobDone())
        return false;
      return assertAreDone(done);
    }
  }

  static List<Job> createJobs(int nbJobs) {
    List<Job> jobs = new ArrayList<Job>();
    for (int i = 0; i < nbJobs; i++)
      jobs.add(new Job());
    return jobs;
  }

  static public void testServerScheduler(ServerScheduler scheduler, int nbJobs, Runnable... clients) {
    if (scheduler.isLocalSchedulingEnabled()) {
      testScheduler(scheduler, nbJobs, clients);
      return;
    }
    ClassResolutionListener listener = new ClassResolutionListener();
    SocketClient.onClassRequested.connect(listener);
    testScheduler(scheduler, nbJobs, clients);
    SocketClient.onClassRequested.disconnect(listener);
    Assert.assertTrue(listener.names.contains(Job.class.getName()));
  }

  static public void testScheduler(Scheduler scheduler, int nbJobs, Runnable... clients) {
    List<Job> jobs = SchedulerTestsUtils.createJobs(nbJobs);
    JobDoneListener listener = createListener();
    Schedulers.addAll(scheduler, jobs, listener);
    scheduler.start();
    startClients(clients);
    scheduler.waitAll();
    Assert.assertTrue(listener.checkJobs(nbJobs));
  }

  private static void startClients(Runnable[] clients) {
    for (Runnable client : clients) {
      Thread thread = new Thread(client);
      thread.setDaemon(true);
      thread.start();
    }
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

  public static boolean assertAreDone(List<? extends Runnable> jobs) {
    for (Runnable job : jobs)
      if (!((Job) job).done)
        return false;
    return true;
  }
}
