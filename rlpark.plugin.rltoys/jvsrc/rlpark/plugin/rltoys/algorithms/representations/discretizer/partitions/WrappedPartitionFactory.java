package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.math.ranges.Range;

public class WrappedPartitionFactory extends AbstractPartitionFactory {
  private static final long serialVersionUID = -5578336702743121475L;

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

  public WrappedPartitionFactory(Range... ranges) {
    super(ranges);
  }

  @Override
  public Discretizer createDiscretizer(int inputIndex, int resolution, int tilingIndex, int nbTilings) {
    Range range = ranges[inputIndex];
    double offset = range.length() / resolution / nbTilings;
    double shift = computeShift(offset, tilingIndex, inputIndex);
    double min = range.min() + shift;
    double max = range.max() + shift;
    return new WrappedPartition(min, max, resolution);
  }
}
