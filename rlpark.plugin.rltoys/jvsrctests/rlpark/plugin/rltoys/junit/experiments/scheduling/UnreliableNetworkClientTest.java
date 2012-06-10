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
import zephyr.plugin.core.api.signals.Listener;

public class UnreliableNetworkClientTest {
  static int nbUnreliableQueue = 0;

  static class UnreliableNetworkQueue extends NetworkJobQueue {
    private final Random random = new Random(nbUnreliableQueue);
    volatile private boolean terminated = false;

    public UnreliableNetworkQueue(String serverHostName, int port) {
      super(serverHostName, port, false);
      nbUnreliableQueue++;
    }

    @Override
    public Runnable request() {
      if (terminated)
        return null;
      Runnable runnable = super.request();
      if (random.nextFloat() < .1) {
        terminated = true;
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
    ServerScheduler scheduler = createServerScheduler(false);
    testServerScheduler(scheduler, 5000, new Runnable() {
      @Override
      public void run() {
        startUnreliableClients(5, false);
      }
    });
    scheduler.dispose();
  }

  static public ServerScheduler createServerScheduler(final boolean useContextClassLoader) throws IOException {
    ServerScheduler scheduler = new ServerScheduler(SchedulerTestsUtils.Port, 0);
    scheduler.onClientDisconnected.connect(new Listener<ServerScheduler>() {
      @Override
      public void listen(ServerScheduler eventInfo) {
        startUnreliableClients(1, useContextClassLoader);
      }
    });
    scheduler.start();
    return scheduler;
  }

  static private UnreliableNetworkQueue newUnreliableQueue() {
    return new UnreliableNetworkQueue(SchedulerTestsUtils.Localhost, SchedulerTestsUtils.Port);
  }

  public static void startUnreliableClients(int nbClients, final boolean useContextClassLoader) {
    for (int i = 0; i < nbClients; i++) {
      final UnreliableNetworkQueue queue = newUnreliableQueue();
      Runnable target = new Runnable() {
        @Override
        public void run() {
          LocalScheduler localScheduler = new LocalScheduler(queue);
          NetworkClient client = new NetworkClient(localScheduler);
          if (useContextClassLoader)
            client.queue().classLoader().setDefaultClassLoader(Thread.currentThread().getContextClassLoader());
          client.run();
          client.dispose();
        }
      };
      Thread thread = new Thread(target, "SpawnClientThread" + nbUnreliableQueue);
      thread.setDaemon(true);
      thread.start();
    }
  }
}
