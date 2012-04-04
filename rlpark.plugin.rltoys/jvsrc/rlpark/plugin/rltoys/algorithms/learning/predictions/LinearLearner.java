package rlpark.plugin.rltoys.algorithms.learning.predictions;

import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public interface LinearLearner {
  void resetWeight(int index);

  PVector weights();

  double error();
}
