package rlpark.plugin.robot.interfaces;

import rlpark.plugin.robot.observations.ObservationVersatileArray;

public interface RobotLog extends RobotProblem {
  boolean hasNextStep();

  ObservationVersatileArray nextStep();
}
