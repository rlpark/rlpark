package rlpark.plugin.rltoys.math.vector;


public interface MutableVector extends RealVector {
  MutableVector clear();

  @Override
  MutableVector copy();

  MutableVector addToSelf(RealVector other);

  MutableVector subtractToSelf(RealVector other);

  MutableVector addToSelf(double factor, RealVector other);

  MutableVector mapMultiplyToSelf(double d);

  void setEntry(int i, double d);

  MutableVector ebeMultiplyToSelf(RealVector other);

  MutableVector set(RealVector other);

  MutableVector set(RealVector other, int start);

  MutableVector ebeDivideToSelf(RealVector other);
}
