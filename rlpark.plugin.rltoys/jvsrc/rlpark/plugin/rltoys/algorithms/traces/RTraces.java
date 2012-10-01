package rlpark.plugin.rltoys.algorithms.traces;

import rlpark.plugin.rltoys.math.vector.BinaryVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;


/**
 * Replacing traces for binary vectors
 */
public class RTraces extends ATraces {
  private static final long serialVersionUID = -324210619484987917L;

  public RTraces() {
    this(DefaultPrototype);
  }

  public RTraces(MutableVector prototype) {
    this(prototype, DefaultThreshold);
  }

  protected RTraces(MutableVector prototype, double threshold) {
    super(prototype, threshold);
  }

  protected RTraces(MutableVector prototype, double threshold, int size) {
    super(prototype, threshold, size);
  }

  @Override
  public RTraces newTraces(int size) {
    return new RTraces(prototype, threshold, size);
  }

  @Override
  protected void updateVector(double lambda, RealVector phi) {
    vector.mapMultiplyToSelf(lambda);
    if (phi instanceof BinaryVector)
      replaceWith((BinaryVector) phi);
    else
      replaceWith((SparseVector) phi);
  }

  private void replaceWith(SparseVector phi) {
    int[] indexes = phi.nonZeroIndexes();
    for (int position = 0; position < phi.nonZeroElements(); position++) {
      int index = indexes[position];
      vector.setEntry(index, phi.getEntry(index));
    }
  }

  private void replaceWith(BinaryVector phi) {
    SVector vector = vect();
    int[] indexes = phi.getActiveIndexes();
    for (int index : indexes)
      vector.setEntry(index, 1.0);
  }

  @Override
  public SVector vect() {
    return (SVector) vector;
  }
}
