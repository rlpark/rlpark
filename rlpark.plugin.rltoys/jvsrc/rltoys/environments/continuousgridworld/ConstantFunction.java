package rltoys.environments.continuousgridworld;

import rltoys.math.ranges.Range;
import zephyr.plugin.core.api.labels.Labeled;

public class ConstantFunction implements ContinuousFunction, Labeled {
  private final double value;
  private final int nbDimension;

  public ConstantFunction(int nbDimension, double value) {
    this.nbDimension = nbDimension;
    this.value = value;
  }

  @Override
  public double fun(double[] position) {
    return value;
  }

  @Override
  public int nbDimension() {
    return nbDimension;
  }

  @Override
  public Range range() {
    return new Range(value - 1, value + 1);
  }

  @Override
  public String label() {
    return "Constant" + String.valueOf(value);
  }
}
