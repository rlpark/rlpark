package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.math.ranges.Range;

public class BoundedBigPartitionFactory extends AbstractPartitionFactory {
  private static final long serialVersionUID = 5982191647323647140L;

  class BoundedBigPartition extends AbstractPartition {
    private static final long serialVersionUID = 237927027724145937L;

    public BoundedBigPartition(double min, double max, int resolution) {
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
    return new BoundedBigPartition(min, max, resolution);
  }
}
