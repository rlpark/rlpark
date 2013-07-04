package rlpark.plugin.rltoys.junit.experiments.scheduling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.ClassLoading;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages;
import rlpark.plugin.rltoys.experiments.scheduling.network.NetworkClient;
import rlpark.plugin.rltoys.experiments.scheduling.network.ServerScheduler;
import rlpark.plugin.rltoys.experiments.scheduling.queue.LocalQueue;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTestsUtils.Job;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTestsUtils.JobDoneListener;
import rlpark.plugin.rltoys.utils.Command;

public class SchedulerTest {
  private static final String Localhost = "localhost";
  private static final int Port = 5000;
  public static final int Timeout = 1000000;

  class DefaultNetworkClient implements Runnable {
    private final int nbLocalThread;

    public DefaultNetworkClient(int nbLocalThread) {
      this.nbLocalThread = nbLocalThread;
    }

    @Override
    public void run() {
      NetworkClient client = new NetworkClient(nbLocalThread, Localhost, Port, false);
      client.run();
      client.dispose();
    }
  }

  @BeforeClass
  static public void junitMode() {
    ClassLoading.enableForceNetworkClassResolution();
    Messages.disableVerbose();
    // Messages.enableDebug();
  }

  @Test(timeout = Timeout)
  public void testJobScheduler() {
    LocalScheduler scheduler = new LocalScheduler(10);
    SchedulerTestsUtils.testScheduler(scheduler, 100000);
    scheduler.dispose();
  }

  @Test(timeout = Timeout)
  public void testJobSchedulerWithManyIterators() {
    LocalScheduler scheduler = new LocalScheduler(10);
    final int NbJobs = 100;
    List<Job> jobs = SchedulerTestsUtils.createJobs(NbJobs);
    JobDoneListener listener = SchedulerTestsUtils.createListener();
    for (Job job : jobs) {
      List<Runnable> oneElement = new ArrayList<Runnable>();
      oneElement.add(job);
      ((LocalQueue) scheduler.queue()).add(oneElement.iterator(), listener);
    }
    scheduler.start();
    scheduler.waitAll();
    Assert.assertEquals(NbJobs, listener.nbJobDone());
    Assert.assertTrue(SchedulerTestsUtils.assertAreDone(listener.jobDone()));
    scheduler.dispose();
  }

  @Test(timeout = Timeout)
  public void testServerScheduler() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port, 10);
    SchedulerTestsUtils.testServerScheduler(scheduler, 10000);
    scheduler.dispose();
  }

  @Test(timeout = Timeout)
  public void testServerSchedulerWithUniqueClient() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port, 0);
    SchedulerTestsUtils.testServerScheduler(scheduler, 1000, new DefaultNetworkClient(1));
    scheduler.dispose();
  }

  @Test(timeout = Timeout)
  public void testServerSchedulerWithUniqueClientMultipleThreads() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port, 0);
    SchedulerTestsUtils.testServerScheduler(scheduler, 10000, new DefaultNetworkClient(2));
    scheduler.dispose();
  }

  @Test(timeout = Timeout)
  public void testServerSchedulerWithMultipleClients() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port, 0);
    SchedulerTestsUtils.testServerScheduler(scheduler, 50000, new DefaultNetworkClient(10));
    scheduler.dispose();
  }

  public static void main(String[] args) throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port, 0);
    final Command command = new Command("jar client", "/usr/bin/java", "-jar", "rltoys-client.jar", "localhost");
    SchedulerTestsUtils.testServerScheduler(scheduler, 1000, new Runnable() {
      @Override
      public void run() {
        try {
          command.start();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    scheduler.dispose();
    command.kill();
  }
}
