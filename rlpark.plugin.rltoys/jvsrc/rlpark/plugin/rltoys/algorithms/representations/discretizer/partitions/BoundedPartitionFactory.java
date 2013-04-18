package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.math.ranges.Range;

public class BoundedPartitionFactory extends AbstractPartitionFactory {
  private static final long serialVersionUID = 5982191647323647140L;

  class BoundedPartition extends AbstractPartition {
    private static final long serialVersionUID = 237927027724145937L;

    public BoundedPartition(double min, double max, int resolution) {
      super(min, max, resolution);
    }

    @Override
    public int discretize(double input) {
      double boundedInput = Math.min(Math.max(input, min), max);
      double n = (boundedInput - min) / intervalWidth;
      if (n < 0)
        n += ((int) (-n / resolution) + 1) * resolution;
      return (int) (n % resolution);
    }
  }

  public BoundedPartitionFactory(double min, double max, int inputSize) {
    super(min, max, inputSize);
  }

  public BoundedPartitionFactory(Range... ranges) {
    super(ranges);
  }

  @Override
  public Discretizer createDiscretizer(int inputIndex, int resolution, int tilingIndex, int nbTilings) {
    Range range = ranges[inputIndex];
    double offset = range.length() / resolution / nbTilings;
    double shift = computeShift(offset, tilingIndex, inputIndex);
    double min = range.min() - shift;
    double max = range.max() - shift + (range.length() / resolution);
    return new BoundedPartition(min, max, resolution);
  }
}
