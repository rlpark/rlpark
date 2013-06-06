package rlpark.plugin.robot.internal.sync;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import rlpark.plugin.robot.observations.ObservationReceiver;
import rlpark.plugin.robot.observations.ObservationVersatile;
import rlpark.plugin.robot.observations.ObservationVersatileArray;

public class ObservationSynchronizer {
  static private int BufferSize = 100;
  private final ObservationReceiver receiver;
  private final LinkedList<ObservationVersatile> lastObsBuffer = new LinkedList<ObservationVersatile>();
  private boolean terminated = false;
  private final Runnable observationReader = new Runnable() {
    @Override
    public void run() {
      observationReaderMainLoop();
    }
  };
  private boolean persistent = false;
  private final Semaphore firstInitialization = new Semaphore(0);

  public ObservationSynchronizer(ObservationReceiver receiver, boolean persistent) {
    assert receiver != null;
    this.receiver = receiver;
    this.persistent = persistent;
    start();
    try {
      if (firstInitialization.tryAcquire(5, TimeUnit.SECONDS))
        firstInitialization.release();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  protected void observationReaderMainLoop() {
    while (!terminated) {
      receiver.initialize();
      firstInitialization.release();
      while (!receiver.isClosed() && !terminated) {
        ObservationVersatile obs = receiver.waitForData();
        if (obs != null)
          setLastObs(obs);
      }
      if (!persistent) {
        terminate();
        break;
      }
    }
  }

  private void start() {
    Thread thread = new Thread(observationReader);
    thread.setName("ObservationReader");
    thread.setDaemon(true);
    thread.start();
  }

  public void setPersistent(boolean persistent) {
    this.persistent = true;
  }

  synchronized private void setLastObs(ObservationVersatile obs) {
    lastObsBuffer.add(obs);
    if (lastObsBuffer.size() > BufferSize)
      lastObsBuffer.poll();
    notifyAll();
  }

  synchronized public ObservationVersatileArray waitNewObs() {
    if (lastObsBuffer.size() > 0)
      return useLastObs();
    try {
      wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return useLastObs();
  }

  synchronized public ObservationVersatileArray newObsNow() {
    return useLastObs();
  }

  synchronized private ObservationVersatileArray useLastObs() {
    if (lastObsBuffer.size() == 0)
      return null;
    ObservationVersatileArray result = new ObservationVersatileArray(lastObsBuffer);
    lastObsBuffer.clear();
    return result;
  }

  public ObservationReceiver receiver() {
    return receiver;
  }

  synchronized public void terminate() {
    receiver.close();
    terminated = true;
    notifyAll();
  }

  public boolean isTerminated() {
    return terminated;
  }
}
