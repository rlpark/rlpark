package rlpark.plugin.rltoys.experiments.scheduling.network;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobQueue;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.ClientInfo;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.MessageJob;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages;
import rlpark.plugin.rltoys.experiments.scheduling.internal.network.NetworkClassLoader;
import rlpark.plugin.rltoys.experiments.scheduling.internal.network.SyncSocket;
import rlpark.plugin.rltoys.experiments.scheduling.queue.LocalQueue;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Chrono;

public class NetworkJobQueue implements JobQueue {
  private static final double MessagePeriod = 1800;
  private final SyncSocket syncSocket;
  private final Map<Runnable, Integer> jobToId = new HashMap<Runnable, Integer>();
  private final NetworkClassLoader classLoader;
  private final Chrono chrono = new Chrono();
  private final Signal<JobDoneEvent> onJobDone = new Signal<JobDoneEvent>();
  private int nbJobsSinceLastMessage = 0;
  private boolean denyNewJobRequest = false;
  private final LocalQueue localQueue = new LocalQueue();

  public NetworkJobQueue(String serverHostName, int port, int nbCore, boolean multipleConnectionAttempts) {
    Socket socket = connectToServer(serverHostName, port, multipleConnectionAttempts);
    syncSocket = new SyncSocket(socket);
    syncSocket.sendClientInfo(new ClientInfo(nbCore));
    classLoader = NetworkClassLoader.newClassLoader(syncSocket);
  }

  private void requestJobsToServer() {
    MessageJob messageJobTodo = syncSocket.jobTransaction(classLoader);
    if (messageJobTodo == null || messageJobTodo.nbJobs() == 0)
      return;
    Runnable[] jobs = messageJobTodo.jobs();
    int[] ids = messageJobTodo.jobIds();
    Set<Integer> addedIds = new HashSet<Integer>();
    ArrayList<Runnable> newJobs = new ArrayList<Runnable>();
    for (int i = 0; i < jobs.length; i++) {
      if (addedIds.contains(ids[i]))
        continue;
      jobToId.put(jobs[i], ids[i]);
      newJobs.add(jobs[i]);
    }
    localQueue.add(newJobs.iterator(), null);
  }

  @Override
  synchronized public Runnable request() {
    if (denyNewJobRequest)
      return null;
    Runnable job = localQueue.request();
    if (job != null)
      return job;
    requestJobsToServer();
    return localQueue.request();
  }

  @Override
  synchronized public void done(Runnable todo, Runnable done) {
    Integer jobId = jobToId.remove(todo);
    if (jobId != null)
      jobDone(done, jobId);
    if (localQueue.areAllDone())
      requestJobsToServer();
    onJobDone.fire(new JobDoneEvent(todo, done));
  }

  private void jobDone(Runnable done, int jobId) {
    syncSocket.write(new MessageJob(jobId, done));
    nbJobsSinceLastMessage += 1;
    if (chrono.getCurrentChrono() > MessagePeriod) {
      Messages.println(nbJobsSinceLastMessage / chrono.getCurrentChrono() + " jobs per seconds");
      chrono.start();
      nbJobsSinceLastMessage = 0;
    }
  }

  public boolean canAnswerJobRequest() {
    return !syncSocket.isClosed() && !denyNewJobRequest;
  }

  @Override
  public Signal<JobDoneEvent> onJobDone() {
    return onJobDone;
  }

  public void denyNewJobRequest() {
    denyNewJobRequest = true;
  }

  public NetworkClassLoader classLoader() {
    return classLoader;
  }

  static private Socket connectToServer(String serverHostName, int port, boolean multipleAttempts) {
    Socket socket = null;
    Random random = null;
    Exception lastException = null;
    Chrono connectionTime = new Chrono();
    while (socket == null) {
      try {
        if (lastException != null)
          System.err.println("Retrying to connect...");
        socket = new Socket(serverHostName, port);
      } catch (Exception e) {
        lastException = e;
        if (!multipleAttempts)
          break;
        if (random == null)
          random = new Random();
        if (connectionTime.getCurrentChrono() > 3600)
          break;
        sleepForConnection(random, 120);
      }
    }
    if (socket == null && lastException != null)
      throw new RuntimeException(lastException);
    if (socket != null && lastException != null)
      System.err.println("Finally connected");
    return socket;
  }

  private static void sleepForConnection(Random random, int maxWaitingTime) {
    long sleepingTime = (long) (random.nextDouble() * maxWaitingTime + 5);
    System.err.println(sleepingTime + "s of sleeping time before another attempt to connect");
    try {
      Thread.sleep(sleepingTime * 1000);
    } catch (InterruptedException e) {
    }
  }

  @Override
  public void dispose() {
    syncSocket.close();
    localQueue.dispose();
    classLoader.dispose();
  }
}
