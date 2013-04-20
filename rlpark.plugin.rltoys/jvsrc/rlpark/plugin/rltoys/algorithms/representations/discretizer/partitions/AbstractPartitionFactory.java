package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.DiscretizerFactory;
import rlpark.plugin.rltoys.math.ranges.Range;

public abstract class AbstractPartitionFactory implements DiscretizerFactory {
  private static final long serialVersionUID = 3356344048646899647L;
  protected final Range[] ranges;
  private double randomShiftRatio = Double.NaN;
  private Random random;

  public AbstractPartitionFactory(Range... ranges) {
    this.ranges = ranges;
  }

  public Range[] rangesClone() {
    return this.ranges.clone();
  }

  public void setRandom(Random random, double randomShiftRatio) {
    this.random = random;
    this.randomShiftRatio = randomShiftRatio;
  }

  protected double computeShift(double offset, int tilingIndex, int inputIndex) {
    double result = tilingIndex * offset;
    if (random != null)
      return result - random.nextFloat() * offset * randomShiftRatio / 2.0;
    return result;
  }
}
