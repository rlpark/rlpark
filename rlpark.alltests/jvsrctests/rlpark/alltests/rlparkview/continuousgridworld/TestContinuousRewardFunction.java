package rlpark.alltests.rlparkview.continuousgridworld;

import zephyr.plugin.core.api.viewable.ContinuousFunction;

public class TestContinuousRewardFunction implements ContinuousFunction {

  @Override
  public double value(double[] position) {
    double sum = 0;
    sum += position[0] > 0 ? 1 : -1;
    sum += position[1] > 0 ? 1 : -1;
    return sum;
  }
}
