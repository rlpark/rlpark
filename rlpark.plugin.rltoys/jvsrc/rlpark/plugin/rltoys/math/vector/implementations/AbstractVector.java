package rlpark.plugin.rltoys.math.vector.implementations;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;

public abstract class AbstractVector implements RealVector {
  private static final long serialVersionUID = 5863507432853349597L;
  public final int size;

  protected AbstractVector(int size) {
    this.size = size;
  }

  @Override
  public MutableVector add(RealVector other) {
    return copyAsMutable().addToSelf(other);
  }

  @Override
  public MutableVector mapMultiply(double d) {
    return copyAsMutable().mapMultiplyToSelf(d);
  }

  @Override
  public MutableVector subtract(RealVector other) {
    return copyAsMutable().subtractToSelf(other);
  }

  @Override
  public MutableVector ebeMultiply(RealVector other) {
    if (other instanceof SparseVector)
      return other.copyAsMutable().ebeMultiplyToSelf(this);
    return copyAsMutable().ebeMultiplyToSelf(other);
  }

  @Override
  public int getDimension() {
    return size;
  }
}
