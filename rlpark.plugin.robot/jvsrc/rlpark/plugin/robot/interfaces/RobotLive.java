package rlpark.plugin.robot.interfaces;


import rlpark.plugin.robot.observations.ObservationVersatile;
import rlpark.plugin.robot.observations.ObservationVersatileArray;
import zephyr.plugin.core.api.synchronization.Closeable;

public interface RobotLive extends Closeable, RobotProblem {
  ObservationVersatileArray waitNewRawObs();

  ObservationVersatile lastReceivedRawObs();
}
