package rlpark.plugin.rltoys.algorithms.representations.acting;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public interface PolicyDistribution extends Policy {
  PVector[] createParameters(int vectorSize);

  RealVector[] getGradLog(RealVector x_t, Action a_t);

  int nbParameterVectors();
}
