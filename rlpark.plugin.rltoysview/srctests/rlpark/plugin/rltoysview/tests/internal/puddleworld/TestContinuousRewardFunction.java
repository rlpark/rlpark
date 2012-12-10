package rlpark.plugin.rltoysview.tests.internal.puddleworld;

import rlpark.plugin.rltoys.algorithms.functions.ContinuousFunction;

public class TestContinuousRewardFunction implements ContinuousFunction {
  @Override
  public double value(double[] input) {
    double sum = 0;
    sum += input[0] > 0 ? 1 : -1;
    sum += input[1] > 0 ? 1 : -1;
    return sum;
  }
}
