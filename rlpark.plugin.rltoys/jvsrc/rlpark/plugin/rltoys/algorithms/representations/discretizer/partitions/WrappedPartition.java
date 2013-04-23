package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.AbstractPartitionFactory.AbstractPartition;

class WrappedPartition extends AbstractPartition {
  private static final long serialVersionUID = -1445471984953765916L;

  public WrappedPartition(double min, double max, int resolution) {
    super(min, max, resolution);
  }

  @Override
  public int discretize(double input) {
    double diff = input - min;
    if (diff < 0)
      diff += (Math.ceil(diff / intervalWidth) + 1) * (max - min);
    assert diff >= 0;
    return (int) ((diff / intervalWidth) % resolution);
  }
}