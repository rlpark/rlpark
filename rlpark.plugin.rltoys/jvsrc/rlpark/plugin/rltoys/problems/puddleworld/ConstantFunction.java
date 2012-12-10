package rlpark.plugin.rltoys.problems.puddleworld;

import rlpark.plugin.rltoys.algorithms.functions.ContinuousFunction;
import zephyr.plugin.core.api.labels.Labeled;

public class ConstantFunction implements ContinuousFunction, Labeled {
  private final double value;

  public ConstantFunction(double value) {
    this.value = value;
  }

  @Override
  public double value(double[] input) {
    return value;
  }

  @Override
  public String label() {
    return "Constant" + String.valueOf(value);
  }
}
