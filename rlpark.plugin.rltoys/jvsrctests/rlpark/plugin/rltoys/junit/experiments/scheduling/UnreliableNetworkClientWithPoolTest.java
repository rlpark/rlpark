package rlpark.plugin.rltoys.junit.experiments.scheduling;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.ClassLoading;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages;
import rlpark.plugin.rltoys.experiments.scheduling.network.ServerScheduler;
import rlpark.plugin.rltoys.experiments.scheduling.pools.PoolResults;
import rlpark.plugin.rltoys.junit.experiments.scheduling.JobPoolTest.JobPoolListenerTest;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTestsUtils.Job;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTestsUtils.JobDoneListener;

public class UnreliableNetworkClientWithPoolTest {
  static int nbUnreliableQueue = 0;

  @BeforeClass
  static public void junitMode() {
    ClassLoading.enableForceNetworkClassResolution();
    Messages.disableVerbose();
    // Messages.enableDebug();
  }

  @Test(timeout = SchedulerTestsUtils.Timeout)
  public void testServerSchedulerWithMultipleClients() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(SchedulerTestsUtils.Port, 0);
    testServerSchedulerWithPool(scheduler, 1000, 100);
    scheduler.dispose();
  }

  private void testServerSchedulerWithPool(ServerScheduler scheduler, int nbJobs, int nbPools) {
    List<Job> jobs = SchedulerTestsUtils.createJobs(nbJobs);
    JobDoneListener jobListener = SchedulerTestsUtils.createListener();
    JobPoolListenerTest poolListener = new JobPoolTest.JobPoolListenerTest();
    PoolResults poolResults = SchedulerTestsUtils.submitJobsInPool(scheduler, jobs, jobListener, poolListener, nbPools);
    scheduler.start();
    UnreliableNetworkClientTest.startUnreliableClients(5, false);
    poolResults.waitPools();
    Assert.assertTrue(SchedulerTestsUtils.assertAreDone(jobListener.jobDone()));
    Assert.assertEquals(nbJobs, jobListener.nbJobDone());
    Assert.assertEquals(nbPools, poolListener.nbPoolDone());
  }
}
