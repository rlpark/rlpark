package rlpark.plugin.rltoys.junit.experiments.scheduling;

import static rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTestsUtils.testServerScheduler;

import java.io.IOException;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.ClassLoading;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages;
import rlpark.plugin.rltoys.experiments.scheduling.network.NetworkClient;
import rlpark.plugin.rltoys.experiments.scheduling.network.NetworkJobQueue;
import rlpark.plugin.rltoys.experiments.scheduling.network.ServerScheduler;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;

public class UnreliableNetworkClientTest {
  static int nbUnreliableQueue = 0;

  static class UnreliableNetworkQueue extends NetworkJobQueue {
    private final Random random = new Random(nbUnreliableQueue);
    volatile private boolean terminated = false;
    public boolean failed = false;

    public UnreliableNetworkQueue(String serverHostName, int port) {
      super(serverHostName, port, 1, false);
      nbUnreliableQueue++;
    }

    @Override
    public Runnable request() {
      if (terminated)
        return null;
      Runnable runnable = super.request();
      if (random.nextFloat() < .1) {
        terminated = true;
        failed = true;
        return null;
      }
      return runnable;
    }

    @Override
    public boolean canAnswerJobRequest() {
      if (!super.canAnswerJobRequest())
        return false;
      return !terminated;
    }

  }

  @BeforeClass
  static public void junitMode() {
    ClassLoading.enableForceNetworkClassResolution();
    Messages.disableVerbose();
    // Messages.enableDebug();
  }

  @Test(timeout = SchedulerTestsUtils.Timeout)
  public void testServerSchedulerWithMultipleClients() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(SchedulerTestsUtils.Port, 0);
    testServerScheduler(scheduler, 2000, new Runnable() {
      @Override
      public void run() {
        startUnreliableClients(4, false);
      }
    });
    scheduler.dispose();
  }

  public static void startUnreliableClients(int nbClients, final boolean useContextClassLoader) {
    for (int i = 0; i < nbClients; i++) {
      Runnable target = new Runnable() {
        @Override
        public void run() {
          boolean queueHasFailed = true;
          NetworkClient client = null;
          try {
            while (queueHasFailed) {
              UnreliableNetworkQueue queue = new UnreliableNetworkQueue(SchedulerTestsUtils.Localhost,
                                                                        SchedulerTestsUtils.Port);
              LocalScheduler localScheduler = new LocalScheduler(queue);
              client = new NetworkClient(localScheduler);
              if (useContextClassLoader)
                client.queue().classLoader().setDefaultClassLoader(Thread.currentThread().getContextClassLoader());
              client.run();
              client.dispose();
              client = null;
              queueHasFailed = queue.failed;
              System.gc();
            }
          } catch (Throwable t) {
          }
          if (client != null)
            client.dispose();
        }
      };
      Thread thread = new Thread(target, "SpawnClientThread" + nbUnreliableQueue);
      thread.setDaemon(true);
      thread.setName("Unreliable Client spawner " + i);
      thread.start();
    }
  }
}
