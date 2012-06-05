package rlpark.plugin.rltoys.algorithms.traces;

import rlpark.plugin.rltoys.math.vector.DenseVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import rlpark.plugin.rltoys.utils.Prototype;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

/**
 * Accumulating traces
 */
public class ATraces implements Traces, Prototype<RealVector> {
  private static final long serialVersionUID = 6241887723527497111L;
  public static final SVector DefaultPrototype = new SVector(0);
  public static final double DefaultThreshold = 1e-8;
  @Monitor
  protected double threshold = 1e-8;
  protected final MutableVector prototype;
  @Monitor
  protected final MutableVector vector;

  public ATraces() {
    this(DefaultPrototype);
  }

  public ATraces(MutableVector prototype) {
    this(prototype, DefaultThreshold);
  }

  public ATraces(MutableVector prototype, double threshold) {
    this(prototype, threshold, 0);
  }

  protected ATraces(MutableVector prototype, double threshold, int size) {
    this.prototype = prototype;
    vector = size > 0 ? prototype.newInstance(size) : null;
  }

  @Override
  public ATraces newTraces(int size) {
    return new ATraces(prototype, threshold, size);
  }

  @Override
  public void update(double lambda, RealVector phi) {
    updateVector(lambda, phi);
    adjustUpdate();
    if (clearRequired(phi, lambda))
      clearBelowThreshold();
    assert Vectors.checkValues(vector);
  }

  protected void adjustUpdate() {
  }

  protected void updateVector(double lambda, RealVector phi) {
    vector.mapMultiplyToSelf(lambda);
    vector.addToSelf(phi);
  }

  private boolean clearRequired(RealVector phi, double lambda) {
    if (threshold == 0)
      return false;
    if (vector instanceof DenseVector)
      return false;
    return true;
  }

  protected void clearBelowThreshold() {
    SVector svector = (SVector) vector;
    double[] values = svector.values;
    int[] indexes = svector.activeIndexes;
    int i = 0;
    while (i < svector.nonZeroElements()) {
      final double absValue = Math.abs(values[i]);
      if (absValue <= threshold)
        svector.removeEntry(indexes[i]);
      else
        i++;
    }
  }

  @Override
  public MutableVector vect() {
    return vector;
  }

  @Override
  public void clear() {
    vector.clear();
  }

  @Override
  public RealVector prototype() {
    return prototype;
  }
}
