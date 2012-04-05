package rlpark.plugin.rltoys.algorithms.traces;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import rlpark.plugin.rltoys.math.vector.VectorEntry;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;

/**
 * Accumulating traces with an absolute value on each element
 */
public class AMaxTraces extends ATraces {
  private static final long serialVersionUID = 8063854269195146376L;
  final private double maximumValue;

  public AMaxTraces() {
    this(1);
  }

  public AMaxTraces(double maximumValue) {
    this(maximumValue, DefaultPrototype);
  }

  public AMaxTraces(double maximumValue, int targetSize, double targetTolerance) {
    this(maximumValue, DefaultPrototype, targetSize, targetTolerance);
  }

  public AMaxTraces(double maximumValue, MutableVector prototype) {
    this(maximumValue, DefaultPrototype, -1, 0);
  }

  public AMaxTraces(double maximumValue, MutableVector prototype, int targetSize, double targetTolerance) {
    this(0, targetSize, targetTolerance, maximumValue, DefaultPrototype);
    assert prototype instanceof SparseVector;
  }

  protected AMaxTraces(int size, int targetSize, double targetTolerance, double maximumValue, MutableVector prototype) {
    super(size, targetSize, targetTolerance, prototype);
    this.maximumValue = maximumValue;
  }

  @Override
  public AMaxTraces newTraces(int size) {
    return new AMaxTraces(size, targetSize, targetTolerance, maximumValue, prototype);
  }

  @Override
  protected void addToSelf(RealVector phi) {
    super.addToSelf(phi);
    if (vector instanceof SVector) {
      SVector svector = (SVector) vector;
      for (int i = 0; i < svector.nonZeroElements(); i++)
        svector.values[i] = adjustValue(svector.values[i]);
      return;
    }
    for (VectorEntry entry : phi) {
      int index = entry.index();
      double value = adjustValue(vector.getEntry(index) + entry.value());
      vector.setEntry(index, value);
    }
  }

  private double adjustValue(double value) {
    if (Math.abs(value) <= maximumValue)
      return value;
    return Math.signum(value) * maximumValue;
  }
}
