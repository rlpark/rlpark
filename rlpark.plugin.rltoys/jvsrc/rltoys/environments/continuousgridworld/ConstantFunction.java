package rltoys.environments.continuousgridworld;

import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.viewable.ContinuousFunction;

public class ConstantFunction implements ContinuousFunction, Labeled {
  private final double value;
  private final int nbDimension;

  public ConstantFunction(int nbDimension, double value) {
    this.nbDimension = nbDimension;
    this.value = value;
  }

  @Override
  public double value(double[] position) {
    return value;
  }

  public int nbDimension() {
    return nbDimension;
  }

  @Override
  public String label() {
    return "Constant" + String.valueOf(value);
  }
}
