package rlpark.plugin.rltoys.algorithms;

import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public interface LinearLearner {
  void resetWeight(int index);

  PVector weights();

  double error();
}
