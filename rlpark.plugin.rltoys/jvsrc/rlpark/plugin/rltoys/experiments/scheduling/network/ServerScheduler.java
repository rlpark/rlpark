package rlpark.plugin.rltoys.experiments.scheduling.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.Scheduler;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages;
import rlpark.plugin.rltoys.experiments.scheduling.internal.network.SocketClient;
import rlpark.plugin.rltoys.experiments.scheduling.internal.serverlog.ServerLog;
import rlpark.plugin.rltoys.experiments.scheduling.queue.LocalQueue;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;

public class ServerScheduler implements Scheduler {
  static final public double StatPeriod = 3600;

  public final class AcceptClientsRunnable implements Runnable {
    private ServerSocket serverSocket = null;
    private boolean terminate = false;

    AcceptClientsRunnable(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
      if (serverSocket == null)
        return;
      Messages.println("Listening on port " + serverSocket.getLocalPort() + "...");
      while (!terminate) {
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
      terminate();
    }

    void terminate() {
      terminate = true;
      if (serverSocket == null)
        return;
      Messages.println("Closing port " + serverSocket.getLocalPort());
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  class JobStatListener implements Listener<JobDoneEvent> {
    @Override
    public void listen(JobDoneEvent eventInfo) {
      serverLog.jobEvent(eventInfo.done);
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
  private final AcceptClientsRunnable acceptClientsRunnable;
  protected final LocalQueue localQueue = new LocalQueue();
  final ServerLog serverLog = new ServerLog();

  private final LocalScheduler localScheduler;
  private final Thread serverThread;
  private final Set<SocketClient> clients = Collections.synchronizedSet(new HashSet<SocketClient>());

  public ServerScheduler() throws IOException {
    this(DefaultPort, LocalScheduler.getDefaultNbThreads());
  }

  public ServerScheduler(int port, int nbLocalThread) throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    acceptClientsRunnable = new AcceptClientsRunnable(serverSocket);
    serverThread = new Thread(acceptClientsRunnable, "AcceptThread");
    serverThread.setDaemon(true);
    localScheduler = nbLocalThread > 0 ? new LocalScheduler(nbLocalThread, localQueue) : null;
    localQueue.onJobDone().connect(new JobStatListener());
    localQueue.enablePoolFromPending();
  }

  synchronized protected void addClient(SocketClient client) {
    clients.add(client);
    client.onClosed.connect(clientClosedListener);
    serverLog.clientEvent(clients, client.clientInfo().hostName + " connected");
    SocketClient.nbJobSendPerRequest(clients.size());
  }

  @Override
  public void waitAll() {
    LocalQueue.waitAllDone(localQueue);
    if (localScheduler != null) {
      Throwable exceptionOccured = localScheduler.exceptionOccured();
      if (exceptionOccured != null)
        throw new RuntimeException(exceptionOccured);
    }
  }

  @Override
  synchronized public void start() {
    serverThread.start();
    if (localScheduler != null)
      localScheduler.start();
  }

  synchronized void removeClient(SocketClient client) {
    boolean removed = clients.remove(client);
    if (!removed)
      return;
    client.onClosed.disconnect(clientClosedListener);
    client.close();
    Collection<Runnable> pendingJobs = new ArrayList<Runnable>(client.pendingJobs());
    for (Runnable pendingJob : pendingJobs)
      localQueue.requestCancel(pendingJob);
    String message = String.format("%s disconnected. Canceling %d job(s). Did %d job(s).",
                                   client.clientInfo().hostName, pendingJobs.size(), client.nbJobDone());
    serverLog.clientEvent(clients, message);
    client.close();
  }

  @Override
  synchronized public void dispose() {
    for (SocketClient client : new ArrayList<SocketClient>(clients))
      removeClient(client);
    acceptClientsRunnable.terminate();
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

  synchronized public void waitClients() {
    System.out.println("All jobs done. Answering to new clients only.");
    try {
      wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
