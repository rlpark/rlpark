package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.math.ranges.Range;

public class BoundedSmallPartitionFactory extends AbstractPartitionFactory {
  private static final long serialVersionUID = 5982191647323647140L;

  public BoundedSmallPartitionFactory(Range... ranges) {
    super(ranges);
  }

  @Override
  public Discretizer createDiscretizer(int inputIndex, int resolution, int tilingIndex, int nbTilings) {
    Range range = ranges[inputIndex];
    double offset = range.length() / ((resolution + 1) * nbTilings - 1);
    double shift = computeShift(offset, tilingIndex, inputIndex);
    double width = range.length() - offset * (nbTilings - 1);
    double min = range.min() + shift;
    double max = min + width;
    return new BoundedPartition(min, max, resolution);
  }
}
