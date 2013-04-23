package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.AbstractPartitionFactory.AbstractPartition;

class BoundedPartition extends AbstractPartition {
  private static final long serialVersionUID = 237927027724145937L;

  public BoundedPartition(double min, double max, int resolution) {
    super(min, max, resolution);
  }

  @Override
  public int discretize(double input) {
    double margin = intervalWidth * .0001;
    double boundedInput = Math.min(Math.max(input, min + margin), max - margin);
    int result = (int) ((boundedInput - min) / intervalWidth);
    assert result >= 0 && result < resolution;
    return result;
  }
}