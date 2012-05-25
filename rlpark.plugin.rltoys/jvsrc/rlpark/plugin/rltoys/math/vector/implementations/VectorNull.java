package rlpark.plugin.rltoys.math.vector.implementations;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class VectorNull implements RealVector {
  private static final long serialVersionUID = 1316689252364870190L;
  private final int size;

  public VectorNull(int size) {
    this.size = size;
  }

  @Override
  public int getDimension() {
    return size;
  }

  @Override
  public double getEntry(int i) {
    return 0;
  }

  @Override
  public double dotProduct(RealVector other) {
    return 0;
  }

  @Override
  public MutableVector mapMultiply(double d) {
    return copyAsMutable();
  }

  @Override
  public MutableVector subtract(RealVector other) {
    return other.copyAsMutable().mapMultiplyToSelf(-1);
  }

  @Override
  public MutableVector add(RealVector other) {
    return other.copyAsMutable();
  }

  @Override
  public MutableVector ebeMultiply(RealVector v) {
    return copyAsMutable();
  }

  @Override
  public MutableVector newInstance(int size) {
    return new SVector(size);
  }

  @Override
  public MutableVector copyAsMutable() {
    return new SVector(size);
  }

  @Override
  public RealVector copy() {
    return this;
  }

  @Override
  public double[] accessData() {
    return new double[size];
  }

}
