package rlpark.plugin.rltoys.experiments.scheduling.network;

import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Chrono;

public class NetworkClient {
  static private double maximumMinutesTime = -1;
  static private String serverHost = "";
  static private int serverPort = ServerScheduler.DefaultPort;
  static private int nbCore = LocalScheduler.getDefaultNbThreads();

  private final LocalScheduler localScheduler;
  final protected NetworkJobQueue networkJobQueue;

  public NetworkClient(int nbThread, String serverHost, int port, boolean multipleAttempts) {
    this(new LocalScheduler(nbThread, createJobQueue(serverHost, port, nbThread, multipleAttempts)));
  }

  public NetworkClient(final LocalScheduler localScheduler) {
    this.localScheduler = localScheduler;
    networkJobQueue = (NetworkJobQueue) localScheduler.queue();
  }

  private static NetworkJobQueue createJobQueue(String serverHost, int port, int nbCore,
      boolean multipleConnectionAttempts) {
    return new NetworkJobQueue(serverHost, port, nbCore, multipleConnectionAttempts);
  }

  private void setMaximumTime(final double wallTime) {
    networkJobQueue.onJobDone().connect(new Listener<JobDoneEvent>() {
      final Chrono chrono = new Chrono();

      @Override
      public void listen(JobDoneEvent event) {
        if (chrono.getCurrentChrono() > wallTime)
          networkJobQueue.denyNewJobRequest();
      }
    });
  }

  public void run() {
    localScheduler.start();
    localScheduler.waitAll();
  }

  public void asyncRun() {
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        NetworkClient.this.run();
      }
    });
    thread.setDaemon(true);
    thread.start();
  }

  public void dispose() {
    localScheduler.dispose();
    networkJobQueue.dispose();
  }

  private static void readParams(String[] args) {
    for (String arg : args)
      if (arg.startsWith("-"))
        readOption(arg);
      else
        readServerInfo(arg);
  }

  private static void readOption(String arg) {
    switch (arg.charAt(1)) {
    case 't':
      maximumMinutesTime = Double.parseDouble(arg.substring(2));
      break;
    case 'c':
      nbCore = Integer.parseInt(arg.substring(2));
      break;
    default:
      System.err.println("Unknown option: " + arg);
    }
  }

  private static void readServerInfo(String arg) {
    int portSeparator = arg.lastIndexOf(":");
    serverHost = portSeparator >= 0 ? arg.substring(0, portSeparator) : arg;
    if (portSeparator >= 0)
      serverPort = Integer.parseInt(arg.substring(portSeparator + 1));
  }

  public static void runClient() {
    NetworkClient scheduler = new NetworkClient(nbCore, serverHost, serverPort, true);
    if (maximumMinutesTime > 0)
      scheduler.setMaximumTime(maximumMinutesTime * 60);
    scheduler.run();
    scheduler.dispose();
  }

  private static void printParams() {
    System.out.println("maximumMinutesTime: " + String.valueOf(maximumMinutesTime));
    System.out.println("nbCore: " + String.valueOf(nbCore));
  }

  public NetworkJobQueue queue() {
    return networkJobQueue;
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: java -jar <jarfile.jar> -t<max time: 30,60,... mins> -c<nb cores> <hostname:port>");
      return;
    }
    readParams(args);
    printParams();
    try {
      runClient();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
