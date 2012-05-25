package rlpark.plugin.rltoys.algorithms.traces;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;

/**
 * Accumulating traces with an absolute value on each element
 */
public class AMaxTraces extends ATraces {
  private static final long serialVersionUID = 8063854269195146376L;
  final static public double DefaultMaximumValue = 1.0;
  private final double maximumValue;

  public AMaxTraces() {
    this(DefaultPrototype);
  }

  public AMaxTraces(MutableVector prototype) {
    this(prototype, DefaultMaximumValue);
  }

  public AMaxTraces(double maximumValue) {
    this(DefaultPrototype, maximumValue, DefaultThreshold);
  }

  public AMaxTraces(MutableVector prototype, double maximumValue) {
    this(prototype, maximumValue, DefaultThreshold);
  }

  public AMaxTraces(MutableVector prototype, double maximumValue, double threshold) {
    this(prototype, maximumValue, threshold, 0);
  }

  protected AMaxTraces(MutableVector prototype, double maximumValue, double threshold, int size) {
    super(prototype, threshold, size);
    this.maximumValue = maximumValue;
  }

  @Override
  public AMaxTraces newTraces(int size) {
    return new AMaxTraces(prototype, maximumValue, threshold, size);
  }

  @Override
  protected void adjustUpdate() {
    final boolean isSVector = vector instanceof SVector;
    double[] data = isSVector ? ((SVector) vector).values : vector.accessData();
    int length = isSVector ? ((SVector) vector).nonZeroElements() : vector.getDimension();
    adjustValues(data, length);
  }

  private void adjustValues(double[] data, int length) {
    for (int i = 0; i < length; i++)
      data[i] = adjustValue(data[i]);
  }

  private double adjustValue(double value) {
    if (Math.abs(value) <= maximumValue)
      return value;
    return Math.signum(value) * maximumValue;
  }
}
