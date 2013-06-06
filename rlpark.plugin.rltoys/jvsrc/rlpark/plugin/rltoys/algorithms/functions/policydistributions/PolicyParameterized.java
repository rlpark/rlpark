package rlpark.plugin.rltoys.algorithms.functions.policydistributions;

import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public interface PolicyParameterized extends PolicyDistribution {
  void setParameters(PVector... parameters);

  PVector[] parameters();
}
