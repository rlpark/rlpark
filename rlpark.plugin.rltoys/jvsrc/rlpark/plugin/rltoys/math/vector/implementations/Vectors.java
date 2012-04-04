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

  static public boolean equals(RealVector a, RealVector b, double margin) {
    if (a == b)
      return true;
    if (a != null && b == null || a == null && b != null)
      return false;
    if (a.getDimension() != b.getDimension())
      return false;
    for (int i = 0; i < a.getDimension(); ++i)
      if (Math.abs(a.getEntry(i) - b.getEntry(i)) > margin)
        return false;
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
}
