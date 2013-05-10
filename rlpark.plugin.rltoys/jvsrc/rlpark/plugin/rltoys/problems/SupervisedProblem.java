package rlpark.plugin.rltoys.problems;

import rlpark.plugin.rltoys.math.vector.RealVector;

public interface SupervisedProblem {
  int inputDimension();

  boolean update();

  double target();

  RealVector input();
}
