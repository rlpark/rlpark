package rlpark.alltests.rlparkview.continuousgridworld;

import rltoys.environments.continuousgridworld.ContinuousFunction;

public class TestContinuousRewardFunction implements ContinuousFunction {

  @Override
  public double fun(double[] position) {
    double sum = 0;
    sum += position[0] > 0 ? 1 : -1;
    sum += position[1] > 0 ? 1 : -1;
    return sum;
  }

  @Override
  public int nbDimension() {
    return 2;
  }
}
