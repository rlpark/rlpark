package rlpark.plugin.rltoys.math.vector;

import java.io.Serializable;

public interface RealVector extends Serializable {
  int getDimension();

  double getEntry(int i);

  double dotProduct(RealVector other);

  MutableVector mapMultiply(double d);

  MutableVector subtract(RealVector other);

  MutableVector add(RealVector other);

  MutableVector ebeMultiply(RealVector v);

  MutableVector newInstance(int size);

  MutableVector copyAsMutable();

  RealVector copy();

  double[] accessData();

  double sum();
}
