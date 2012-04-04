package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.math.ranges.Range;

public abstract class AbstractPartition extends Range implements Discretizer {
  private static final long serialVersionUID = 5477929434176764517L;
  public final int resolution;
  public final double partLength;

  public AbstractPartition(double min, double max, int resolution) {
    super(min, max);
    this.resolution = resolution;
    partLength = length() / resolution;
  }

  @Override
  public String toString() {
    return String.format("%f:%d:%f", min(), resolution, max());
  }

  @Override
  public int resolution() {
    return resolution;
  }

  @Override
  public int hashCode() {
    return super.hashCode() + resolution;
  }

  @Override
  public boolean equals(Object object) {
    if (!super.equals(object))
      return false;
    return ((AbstractPartition) object).resolution == resolution;
  }

  @Override
  abstract public int discretize(double input);
}
