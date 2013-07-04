package rlpark.plugin.rltoys.junit.experiments.scheduling;

import java.util.List;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobPool;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobPool.JobPoolListener;
import rlpark.plugin.rltoys.experiments.scheduling.pools.FileJobPool;
import rlpark.plugin.rltoys.experiments.scheduling.pools.MemoryJobPool;
import rlpark.plugin.rltoys.experiments.scheduling.pools.PoolResults;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTestsUtils.Job;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTestsUtils.JobDoneListener;
import zephyr.plugin.core.api.signals.Listener;

public class JobPoolTest {
  static public class JobPoolListenerTest implements JobPoolListener {
    int poolDone = 0;

    @Override
    public void listen(JobPool pool) {
      poolDone++;
    }

    public int nbPoolDone() {
      return poolDone;
    }
  }

  interface PoolFactory {
    JobPool createPool(JobPoolListenerTest poolListener, Listener<JobDoneEvent> jobListener);
  }

  final static private int NbJobs = 100;
  final static private int NbPool = 5;

  @Test
  public void testMemoryJobPool() {
    testJobPool(new PoolFactory() {
      @Override
      public JobPool createPool(JobPoolListenerTest poolListener, Listener<JobDoneEvent> jobListener) {
        return new MemoryJobPool(poolListener, jobListener);
      }
    });
  }

  @Test
  public void testFileJobPool() {
    testJobPool(new PoolFactory() {
      @Override
      public JobPool createPool(JobPoolListenerTest poolListener, Listener<JobDoneEvent> jobListener) {
        return new FileJobPool(poolListener, jobListener);
      }
    });
  }

  private void testJobPool(PoolFactory poolFactory) {
    LocalScheduler scheduler = new LocalScheduler(10);
    JobDoneListener jobListener = SchedulerTestsUtils.createListener();
    JobPoolListenerTest poolListener = new JobPoolListenerTest();
    PoolResults poolResults = new PoolResults();
    for (int i = 0; i < NbPool; i++) {
      JobPool jobPool = preparePool(poolFactory, poolListener, jobListener);
      poolResults.add(jobPool.submitTo(scheduler));
    }
    scheduler.start();
    scheduler.waitAll();
    poolResults.waitPools();
    Assert.assertEquals(NbPool, poolListener.poolDone);
    Assert.assertEquals(NbJobs * NbPool, jobListener.nbJobDone());
    Assert.assertTrue(SchedulerTestsUtils.assertAreDone(jobListener.jobDone()));
    scheduler.dispose();
  }

  private JobPool preparePool(PoolFactory poolFactory, JobPoolListenerTest poolListener,
      Listener<JobDoneEvent> jobListener) {
    List<Job> jobs = SchedulerTestsUtils.createJobs(NbJobs);
    JobPool pool = poolFactory.createPool(poolListener, jobListener);
    for (Job job : jobs)
      pool.add(job);
    return pool;
  }
}
