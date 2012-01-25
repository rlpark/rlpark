package rltoys.experiments.scheduling;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import rltoys.experiments.scheduling.JobPoolTest.JobPoolListenerTest;
import rltoys.experiments.scheduling.SchedulerTestsUtils.Job;
import rltoys.experiments.scheduling.SchedulerTestsUtils.JobDoneListener;
import rltoys.experiments.scheduling.internal.messages.ClassLoading;
import rltoys.experiments.scheduling.internal.messages.Messages;
import rltoys.experiments.scheduling.network.ServerScheduler;
import rltoys.experiments.scheduling.pools.PoolResults;

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
    ServerScheduler scheduler = UnreliableNetworkClientTest.createServerScheduler(false);
    UnreliableNetworkClientTest.startUnreliableClients(5, false);
    testServerSchedulerWithPool(scheduler, 1000, 100);
    scheduler.dispose();
  }

  private void testServerSchedulerWithPool(ServerScheduler scheduler, int nbJobs, int nbPools) {
    List<Job> jobs = SchedulerTestsUtils.createJobs(nbJobs);
    JobDoneListener jobListener = SchedulerTestsUtils.createListener();
    JobPoolListenerTest poolListener = new JobPoolTest.JobPoolListenerTest();
    PoolResults poolResults = SchedulerTestsUtils.submitJobsInPool(scheduler, jobs, jobListener, poolListener, nbPools);
    scheduler.start();
    poolResults.waitPools();
    Assert.assertTrue(SchedulerTestsUtils.assertAreDone(jobListener.jobDone()));
    Assert.assertEquals(nbJobs, jobListener.nbJobDone());
    Assert.assertEquals(nbPools, poolListener.nbPoolDone());
  }
}
