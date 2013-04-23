package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.math.ranges.Range;

public class WrappedPartitionFactory extends AbstractPartitionFactory {
  private static final long serialVersionUID = -5578336702743121475L;

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
