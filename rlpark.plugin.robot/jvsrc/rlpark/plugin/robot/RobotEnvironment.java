package rlpark.plugin.robot;


import rlpark.plugin.rltoys.agents.Agent;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.robot.sync.ObservationReceiver;
import rlpark.plugin.robot.sync.ObservationSynchronizer;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rlpark.plugin.robot.sync.ObservationVersatileArray;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.synchronization.Clock;

public abstract class RobotEnvironment implements RobotLive, Labeled {
  protected final ObservationSynchronizer obsSync;
  protected ObservationVersatile lastReceivedObsBuffer;

  public RobotEnvironment(ObservationReceiver receiver, boolean persistent) {
    obsSync = new ObservationSynchronizer(receiver, persistent);
  }

  public ObservationSynchronizer synchronizer() {
    return obsSync;
  }

  protected ObservationReceiver receiver() {
    return obsSync.receiver();
  }

  public double[] newObsNow() {
    return newRawObsNow().doubleValues();
  }

  public ObservationVersatileArray newRawObsNow() {
    ObservationVersatileArray newObs = obsSync.newObsNow();
    if (newObs.last() != null)
      lastReceivedObsBuffer = newObs.last();
    return newObs;
  }

  public double[] waitNewObs() {
    return waitNewRawObs().doubleValues();
  }

  @Override
  public ObservationVersatileArray waitNewRawObs() {
    ObservationVersatileArray observations = obsSync.waitNewObs();
    if (observations == null) {
      lastReceivedObsBuffer = null;
      close();
      return null;
    }
    if (observations.last() != null)
      lastReceivedObsBuffer = observations.last();
    return observations;
  }

  @Override
  public void close() {
    obsSync.terminate();
  }

  @Override
  public String label() {
    return getClass().getSimpleName();
  }

  public double[] lastReceivedObs() {
    return lastReceivedObsBuffer != null ? lastReceivedObsBuffer.doubleValues() : null;
  }

  @Override
  public ObservationVersatile lastReceivedRawObs() {
    return lastReceivedObsBuffer;
  }

  @Override
  public int observationPacketSize() {
    return receiver().packetSize();
  }

  public boolean isClosed() {
    return obsSync.isTerminated();
  }

  /**
   * Do not use this method, use your own main loop instead using sendAction(),
   * setLed() waitNewObs() and lastReceivedObs()
   * 
   * @see rlpark.plugin.robot.RobotEnvironment#run(zephyr.plugin.core.api.synchronization
   *      .Clock, rlpark.plugin.rltoys.agents.Agent)
   */
  @Deprecated
  public abstract void run(Clock clock, Agent agent);

  abstract public void sendAction(Action a);
}
