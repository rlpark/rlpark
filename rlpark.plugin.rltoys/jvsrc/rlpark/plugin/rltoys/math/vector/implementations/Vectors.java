package rlpark.plugin.rltoys.math.vector.implementations;

import rlpark.plugin.rltoys.math.vector.DenseVector;
import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import rlpark.plugin.rltoys.math.vector.VectorEntry;
import rlpark.plugin.rltoys.utils.NotImplemented;
import rlpark.plugin.rltoys.utils.Utils;

public class Vectors {
  static public boolean equals(RealVector a, RealVector b) {
    return equals(a, b, Float.MIN_VALUE);
  }

  static public double diff(RealVector a, RealVector b) {
    double diff = 0;
    for (int i = 0; i < a.getDimension(); ++i)
      diff = Math.max(diff, Math.abs(a.getEntry(i) - b.getEntry(i)));
    return diff;
  }

  static public boolean equals(RealVector a, RealVector b, double margin) {
    if (a == b)
      return true;
    if (a != null && b == null || a == null && b != null)
      return false;
    if (a.getDimension() != b.getDimension())
      return false;
    for (int i = 0; i < a.getDimension(); ++i) {
      final double diff = Math.abs(a.getEntry(i) - b.getEntry(i));
      if (diff > margin)
        return false;
    }
    return true;
  }

  public static boolean checkValues(RealVector v) {
    for (VectorEntry entry : v)
      if (!Utils.checkValue(entry.value()))
        return false;
    return true;
  }

  public static void clear(MutableVector vector) {
    if (vector instanceof DenseVector) {
      ((DenseVector) vector).set(0.0);
      return;
    }
    if (vector instanceof SparseVector) {
      ((SparseVector) vector).clear();
      return;
    }
    throw new NotImplemented();
  }

  static public MutableVector absToSelf(MutableVector v) {
    if (v instanceof SVector) {
      absToSelf(((SVector) v).values);
      return v;
    }
    if (v instanceof PVector) {
      absToSelf(((PVector) v).data);
      return v;
    }
    for (VectorEntry entry : v)
      v.setEntry(entry.index(), Math.abs(entry.value()));
    return v;
  }

  static public void absToSelf(double[] data) {
    for (int i = 0; i < data.length; i++)
      data[i] = Math.abs(data[i]);
  }

  static public double sum(RealVector v) {
    double sum = 0.0;
    for (VectorEntry entry : v)
      sum += entry.value();
    return sum;
  }

  public static MutableVector toBinary(RealVector v) {
    if (v instanceof SVector) {
      SVector result = (SVector) v.copyAsMutable();
      result.set(1.0);
      return result;
    }
    SVector result = new SVector(v.getDimension());
    double[] data = v.accessData();
    for (int i = 0; i < data.length; i++)
      if (data[i] != 0)
        result.setEntry(i, 1);
    return result;
  }

  public static boolean isNull(RealVector v) {
    for (double value : v.accessData())
      if (value != 0)
        return false;
    return true;
  }
}
