package rlpark.plugin.rltoys.algorithms.functions.policydistributions;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public interface PolicyDistribution extends Policy {
  PVector[] createParameters(int vectorSize);

  RealVector[] computeGradLog(Action a);

  int nbParameterVectors();
}
