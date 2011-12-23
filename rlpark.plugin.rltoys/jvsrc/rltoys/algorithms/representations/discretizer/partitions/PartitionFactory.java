package rltoys.algorithms.representations.discretizer.partitions;

import java.util.Random;

import rltoys.algorithms.representations.discretizer.Discretizer;
import rltoys.algorithms.representations.discretizer.DiscretizerFactory;
import rltoys.math.ranges.Range;

public class PartitionFactory implements DiscretizerFactory {
  private static final long serialVersionUID = 3356344048646899647L;
  private final Range[] ranges;
  private double randomShiftRatio = Double.NaN;
  private Random random;
  private final boolean wrapped;

  public PartitionFactory(boolean wrapped, Range... ranges) {
    this.wrapped = wrapped;
    this.ranges = ranges;
  }

  public PartitionFactory(boolean wrapped, double min, double max, int inputSize) {
    this(wrapped, getRanges(min, max, inputSize));
  }

  public void setRandom(Random random, double randomShiftRatio) {
    this.random = random;
    this.randomShiftRatio = randomShiftRatio;
  }

  @Override
  public Discretizer createDiscretizer(int inputIndex, int resolution, int tilingIndex, int nbTilings) {
    Range range = ranges[inputIndex];
    double offset = range.length() / resolution / nbTilings;
    double shift = computeShift(offset, tilingIndex, inputIndex);
    return wrapped ? wrappedPartition(resolution, range, shift) : boundedPartition(resolution, range, shift);
  }

  private Discretizer wrappedPartition(int resolution, Range range, double shift) {
    double min = range.min() + shift;
    double max = range.max() + shift;
    return new WrappedPartition(min, max, resolution);
  }

  private Discretizer boundedPartition(int resolution, Range range, double shift) {
    double min = range.min() - shift;
    double max = range.max() - shift + (range.length() / resolution);
    return new BoundedPartition(min, max, resolution + 1);
  }

  public static Range[] getRanges(double min, double max, int stateSize) {
    Range[] ranges = new Range[stateSize];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = new Range(min, max);
    return ranges;
  }

  private double computeShift(double offset, int tilingIndex, int inputIndex) {
    double result = tilingIndex * offset;
    if (random != null)
      return result - random.nextFloat() * offset * randomShiftRatio / 2.0;
    return result;
  }
}
