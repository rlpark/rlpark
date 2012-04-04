package rlpark.plugin.robot;

import rlpark.plugin.rltoys.envio.observations.Legend;

public interface RobotProblem {
  Legend legend();

  int observationPacketSize();
}
