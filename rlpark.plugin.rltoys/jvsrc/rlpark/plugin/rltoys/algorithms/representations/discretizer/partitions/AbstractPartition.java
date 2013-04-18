package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;

public abstract class AbstractPartition implements Discretizer {
  private static final long serialVersionUID = 5477929434176764517L;
  public final int resolution;
  public final double intervalWidth;
  public final double min;
  public final double max;

  public AbstractPartition(double min, double max, int resolution) {
    this.min = min;
    this.max = max;
    this.resolution = resolution;
    intervalWidth = (max - min) / resolution;
  }

  @Override
  public String toString() {
    return String.format("%f:%d:%f", min, resolution, max);
  }

  @Override
  public int resolution() {
    return resolution;
  }

  @Override
  abstract public int discretize(double input);
}
