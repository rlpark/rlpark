package rlpark.plugin.rltoys.experiments.scheduling.internal.messages;

import java.io.Serializable;
import java.net.UnknownHostException;

public class ClientInfo implements Serializable {
  private static final long serialVersionUID = 5234119951047042421L;
  final public int nbThreads;
  final public int nbCores;
  final public String hostName;

  public ClientInfo(int nbThread) {
    this.nbThreads = nbThread;
    this.nbCores = Runtime.getRuntime().availableProcessors();
    this.hostName = getLocalHostName();
  }

  static private String getLocalHostName() {
    String localhostName = "unknown";
    try {
      localhostName = java.net.InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      Messages.displayError(e);
    }
    return localhostName;
  }
}
