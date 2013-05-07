package rlpark.plugin.rltoys.problems;

import rlpark.plugin.rltoys.math.vector.RealVector;

public interface SupervisedProblem {
  int inputDimension();

  void update();

  double target();

  RealVector input();
}
