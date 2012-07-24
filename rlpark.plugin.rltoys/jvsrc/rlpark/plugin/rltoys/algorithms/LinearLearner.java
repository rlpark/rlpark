package rlpark.plugin.rltoys.algorithms;

import rlpark.plugin.rltoys.algorithms.functions.ParameterizedFunction;

public interface LinearLearner extends ParameterizedFunction {
  void resetWeight(int index);

  double error();
}
