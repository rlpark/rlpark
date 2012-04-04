package rlpark.plugin.rltoys.problems.continuousgridworld;

import rlpark.plugin.rltoys.math.normalization.MinMaxNormalizer;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.viewable.ContinuousFunction;

@Monitor
public class NormalizedFunction implements ContinuousFunction {
  private final ContinuousFunction function;
  private final MinMaxNormalizer normalizer = new MinMaxNormalizer();
  private double normalizedRewardValue;
  private double rewardValue;

  public NormalizedFunction(ContinuousFunction function) {
    this.function = function;
  }

  @Override
  public double value(double[] input) {
    rewardValue = function.value(input);
    normalizer.update(rewardValue);
    normalizedRewardValue = normalizer.normalize(rewardValue);
    return normalizedRewardValue;
  }

  public ContinuousFunction function() {
    return function;
  }
}
