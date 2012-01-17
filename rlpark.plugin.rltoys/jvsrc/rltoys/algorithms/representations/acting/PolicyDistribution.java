package rltoys.algorithms.representations.acting;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;

public interface PolicyDistribution extends Policy {
  PVector[] createParameters(int vectorSize);

  RealVector[] getGradLog(RealVector x_t, Action a_t);

  int nbParameterVectors();
}
