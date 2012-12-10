package rlpark.plugin.rltoys.experiments.scheduling.internal.serverlog;

import java.io.IOException;
import java.util.Set;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.TimedJob;
import rlpark.plugin.rltoys.experiments.scheduling.internal.messages.Messages;
import rlpark.plugin.rltoys.experiments.scheduling.internal.network.SocketClient;
import zephyr.plugin.core.api.internal.monitoring.fileloggers.LoggerRow;
import zephyr.plugin.core.api.synchronization.Chrono;

@SuppressWarnings("restriction")
public class ServerLog {
  private final LoggerRow clientsLog;
  private final LoggerRow jobsLog;
  private final Chrono chrono = new Chrono();
  private long cumulatedTime = 0;

  public ServerLog() {
    try {
      clientsLog = new LoggerRow("./clients.logtxt");
      jobsLog = new LoggerRow("./jobs.logtxt");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    clientsLog.writeLegend("RealTime", "Clients", "Threads", "Cores");
    jobsLog.writeLegend("RealTime", "CumulatedTime", "Ratio");
  }

  public void clientEvent(Set<SocketClient> clients, String message) {
    int nbCores = 0;
    int nbThreads = 0;
    for (SocketClient client : clients) {
      nbCores += client.clientInfo().nbCores;
      nbThreads += client.clientInfo().nbThreads;
    }
    clientsLog.writeRow(chrono.getCurrentMillis(), clients.size(), nbThreads, nbCores);
    Messages.println(String.format("%s %d[%d] client%s", message, clients.size(), nbThreads, clients.size() > 1 ? "s"
        : ""));
  }

  public void jobEvent(Runnable done) {
    if (!(done instanceof TimedJob))
      return;
    cumulatedTime += ((TimedJob) done).getComputationTimeMillis();
    double ratio = cumulatedTime / (double) chrono.getCurrentMillis();
    jobsLog.writeRow(chrono.getCurrentMillis(), cumulatedTime, ratio);
  }

  public void close() {
    clientsLog.close();
    jobsLog.close();
  }
}
