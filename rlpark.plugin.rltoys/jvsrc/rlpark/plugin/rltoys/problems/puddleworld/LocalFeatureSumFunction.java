package rlpark.plugin.rltoys.problems.puddleworld;

import rlpark.plugin.rltoys.algorithms.functions.ContinuousFunction;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class LocalFeatureSumFunction implements ContinuousFunction {
  private final ContinuousFunction[] features;
  @Monitor
  private final double[] weights;
  private final double baseReward;

  public LocalFeatureSumFunction(double[] weights, ContinuousFunction[] features, double baseReward) {
    this.weights = weights;
    this.features = features;
    this.baseReward = baseReward;
  }

  @Override
  public double value(double[] input) {
    double sum = 0.0;
    for (int i = 0; i < features.length; i++)
      sum += weights[i] * features[i].value(input);
    return sum + baseReward;
  }

  public double[] weights() {
    return weights;
  }

  public ContinuousFunction[] features() {
    return features;
  }

  public int size() {
    return features.length;
  }
}
