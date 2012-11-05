package rlpark.plugin.rltoys.problems.puddleworld;

import rlpark.plugin.rltoys.algorithms.functions.ContinuousFunction;

public class RewardWhenTerminated implements ContinuousFunction {
  private final TerminationFunction terminationFunction;

  public RewardWhenTerminated(TerminationFunction terminationFunction) {
    this.terminationFunction = terminationFunction;
  }

  @Override
  public double value(double[] position) {
    return terminationFunction.isTerminated(position) ? 1.0 : 0.0;
  }

}
