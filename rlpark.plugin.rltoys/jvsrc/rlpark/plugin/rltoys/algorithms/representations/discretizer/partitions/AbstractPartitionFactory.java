package rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.representations.discretizer.Discretizer;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.DiscretizerFactory;
import rlpark.plugin.rltoys.math.ranges.Range;

public abstract class AbstractPartitionFactory implements DiscretizerFactory {
  static public abstract class AbstractPartition implements Discretizer {
    private static final long serialVersionUID = 5477929434176764517L;
    public final int resolution;
    public final double intervalWidth;
    public final double min;
    public final double max;

    public AbstractPartition(double min, double max, int resolution) {
      this.min = min;
      this.max = max;
      this.resolution = resolution;
      intervalWidth = (max - min) / resolution;
    }

    @Override
    public String toString() {
      return String.format("[%f:%f]/%d", min, max, resolution);
    }

    @Override
    public int resolution() {
      return resolution;
    }

    @Override
    abstract public int discretize(double input);
  }

  private static final long serialVersionUID = 3356344048646899647L;
  protected final Range[] ranges;
  private double randomShiftRatio = Double.NaN;
  private Random random;

  public AbstractPartitionFactory(Range... ranges) {
    this.ranges = ranges;
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
