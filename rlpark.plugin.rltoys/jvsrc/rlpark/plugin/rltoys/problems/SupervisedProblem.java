package rlpark.plugin.rltoys.problems;

import rlpark.plugin.rltoys.math.vector.RealVector;

public interface SupervisedProblem {
  void update();

  double target();

  RealVector input();
}
