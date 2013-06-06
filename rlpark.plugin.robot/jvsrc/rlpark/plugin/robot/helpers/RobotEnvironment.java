package rlpark.plugin.robot.helpers;


import rlpark.plugin.robot.interfaces.RobotLive;
import rlpark.plugin.robot.internal.sync.ObservationSynchronizer;
import rlpark.plugin.robot.observations.ObservationReceiver;
import rlpark.plugin.robot.observations.ObservationVersatile;
import rlpark.plugin.robot.observations.ObservationVersatileArray;
import zephyr.plugin.core.api.labels.Labeled;

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
    ObservationVersatileArray rawObs = newRawObsNow();
    return rawObs != null ? rawObs.doubleValues() : null;
  }

  public ObservationVersatileArray newRawObsNow() {
    ObservationVersatileArray newObs = obsSync.newObsNow();
    if (newObs != null)
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
      // close(); JVM - removed this as it causes a lost observation to cause
      // more problems.
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
}
