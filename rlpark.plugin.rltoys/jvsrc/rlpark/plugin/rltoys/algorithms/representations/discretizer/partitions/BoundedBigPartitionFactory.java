package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.math.ranges.Range;

public class BoundedBigPartitionFactory extends AbstractPartitionFactory {
  private static final long serialVersionUID = 5982191647323647140L;

  public BoundedBigPartitionFactory(Range... ranges) {
    super(ranges);
  }

  @Override
  public Discretizer createDiscretizer(int inputIndex, int resolution, int tilingIndex, int nbTilings) {
    Range range = ranges[inputIndex];
    double offset = range.length() / (resolution * nbTilings - nbTilings + 1);
    double shift = computeShift(offset, tilingIndex, inputIndex);
    double width = offset * nbTilings * resolution;
    double min = range.min() - shift;
    double max = min + width;
    return new BoundedPartition(min, max, resolution);
  }
}
