package rltoys.environments.continuousgridworld;

import zephyr.plugin.core.api.viewable.ContinuousFunction;

public class RewardWhenTerminated implements ContinuousFunction {
  private final TargetReachedTermination terminationFunction;

  public RewardWhenTerminated(TargetReachedTermination terminationFunction) {
    this.terminationFunction = terminationFunction;
  }

  @Override
  public double value(double[] position) {
    return terminationFunction.isTerminated(position) ? 1.0 : 0.0;
  }

}
