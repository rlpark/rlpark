package rltoys.experiments.scheduling.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.internal.messages.Messages;
import rltoys.experiments.scheduling.internal.network.SocketClient;
import rltoys.experiments.scheduling.queue.LocalQueue;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Chrono;

public class ServerScheduler implements Scheduler {
  static final public double StatPeriod = 3600;

  class JobStatListener implements Listener<JobDoneEvent> {
    private final Chrono chrono = new Chrono();
    private double lastChronoValue = 0.0;

    @Override
    public void listen(JobDoneEvent eventInfo) {
      if (chrono.getCurrentChrono() - lastChronoValue < StatPeriod)
        return;
      lastChronoValue = chrono.getCurrentChrono();
      double nbJobPerSecond = localQueue.nbJobsDone() / lastChronoValue;
      System.out.printf("%f jobs per second. ", nbJobPerSecond);
      System.out.println();
      chrono.start();
    }
  }

  static final public int DefaultPort = 5000;
  static public boolean serverVerbose = true;
  public Signal<ServerScheduler> onClientDisconnected = new Signal<ServerScheduler>();
  private final Listener<SocketClient> clientClosedListener = new Listener<SocketClient>() {
    @Override
    public void listen(SocketClient client) {
      removeClient(client);
      onClientDisconnected.fire(ServerScheduler.this);
    }
  };
  private final Runnable acceptClientsRunnable = new Runnable() {
    @Override
    public void run() {
      Messages.println("Listening on port " + serverSocket.getLocalPort() + "...");
      while (!serverSocket.isClosed()) {
        try {
          Socket clientSocket = serverSocket.accept();
          SocketClient socketClient = new SocketClient(localQueue, clientSocket);
          if (!socketClient.readName()) {
            socketClient.close();
            continue;
          }
          addClient(socketClient);
          socketClient.start();
        } catch (IOException e) {
        }
      }
    }
  };
  protected final LocalQueue localQueue = new LocalQueue();
  final ServerSocket serverSocket;
  private final LocalScheduler localScheduler;
  private final Thread serverThread = new Thread(acceptClientsRunnable, "AcceptThread");
  private final Set<SocketClient> clients = Collections.synchronizedSet(new HashSet<SocketClient>());

  public ServerScheduler() throws IOException {
    this(DefaultPort, LocalScheduler.getDefaultNbThreads());
  }

  public ServerScheduler(int port, int nbLocalThread) throws IOException {
    serverSocket = new ServerSocket(port);
    serverThread.setDaemon(true);
    localScheduler = nbLocalThread > 0 ? new LocalScheduler(nbLocalThread, localQueue) : null;
  }

  synchronized protected void addClient(SocketClient client) {
    clients.add(client);
    client.onClosed.connect(clientClosedListener);
    printConnectionInfo(client.clientName() + " connected");
    SocketClient.nbJobSendPerRequest(clients.size());
  }

  protected void printConnectionInfo(String news) {
    Messages.println(String.format("%s %d client%s.", news, clients.size(), clients.size() > 1 ? "s" : ""));
  }

  @Override
  public void runAll() {
    JobStatListener listener = new JobStatListener();
    localQueue.onJobDone().connect(listener);
    start();
    LocalQueue.waitAllDone(localQueue);
    if (localScheduler != null) {
      Throwable exceptionOccured = localScheduler.exceptionOccured();
      if (exceptionOccured != null)
        throw new RuntimeException(exceptionOccured);
    }
    localQueue.onJobDone().disconnect(listener);
  }

  synchronized public void start() {
    if (!serverThread.isAlive())
      serverThread.start();
    if (localScheduler != null)
      localScheduler.start();
    for (SocketClient clientScheduler : new ArrayList<SocketClient>(clients))
      clientScheduler.wakeUp();
  }

  synchronized void removeClient(SocketClient client) {
    boolean removed = clients.remove(client);
    if (!removed)
      return;
    client.onClosed.disconnect(clientClosedListener);
    Collection<Runnable> pendingJobs = client.pendingJobs();
    for (Runnable pendingJob : pendingJobs)
      localQueue.requestCancel(pendingJob);
    printConnectionInfo(String.format("%s disconnected. Canceling %d job%s. Did %d job%s.", client.clientName(),
                                      pendingJobs.size(), pendingJobs.size() > 1 ? "s" : "", client.nbJobDone(),
                                      client.nbJobDone() > 1 ? "s" : ""));
  }

  @Override
  synchronized public void dispose() {
    for (SocketClient client : new ArrayList<SocketClient>(clients))
      removeClient(client);
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (localScheduler != null)
      localScheduler.dispose();
    localQueue.dispose();
  }

  public boolean isLocalSchedulingEnabled() {
    return localScheduler != null;
  }

  @Override
  public LocalQueue queue() {
    return localQueue;
  }
}
