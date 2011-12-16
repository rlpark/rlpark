package rltoys.environments.continuousgridworld;

import zephyr.plugin.core.api.viewable.ContinuousFunction;

public class LocalFeatureSumFunction implements ContinuousFunction {
  private final ContinuousFunction[] features;
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
}
