package rltoys.environments.continuousgridworld;

import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.viewable.ContinuousFunction;

public class ConstantFunction implements ContinuousFunction, Labeled {
  private final double value;

  public ConstantFunction(double value) {
    this.value = value;
  }

  @Override
  public double value(double[] position) {
    return value;
  }

  @Override
  public String label() {
    return "Constant" + String.valueOf(value);
  }
}
