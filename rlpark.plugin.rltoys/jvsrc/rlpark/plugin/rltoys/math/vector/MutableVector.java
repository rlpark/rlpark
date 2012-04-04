package rlpark.plugin.rltoys.math.vector;


public interface MutableVector extends RealVector {
  @Override
  MutableVector copy();

  MutableVector addToSelf(RealVector other);

  MutableVector subtractToSelf(RealVector other);

  MutableVector mapMultiplyToSelf(double d);

  void setEntry(int i, double d);

  MutableVector ebeMultiplyToSelf(RealVector phi_t);
}
