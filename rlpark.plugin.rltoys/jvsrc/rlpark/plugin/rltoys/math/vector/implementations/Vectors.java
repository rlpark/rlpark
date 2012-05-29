package rlpark.plugin.rltoys.math.vector.implementations;


import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.SparseVector;
import rlpark.plugin.rltoys.utils.NotImplemented;
import rlpark.plugin.rltoys.utils.Utils;

public class Vectors {
  static public boolean equals(RealVector a, RealVector b) {
    return equals(a, b, 0);
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
    for (int i = 0; i < a.getDimension(); i++) {
      final double diff = Math.abs(a.getEntry(i) - b.getEntry(i));
      if (diff > margin)
        return false;
    }
    return true;
  }

  public static boolean checkValues(RealVector v) {
    for (int i = 0; i < v.getDimension(); i++)
      if (!Utils.checkValue(v.getEntry(i)))
        return false;
    return true;
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
    throw new NotImplemented();
  }

  static public void absToSelf(double[] data) {
    for (int i = 0; i < data.length; i++)
      data[i] = Math.abs(data[i]);
  }

  static public double sum(RealVector v) {
    double sum = 0.0;
    for (int i = 0; i < v.getDimension(); i++)
      sum += v.getEntry(i);
    return sum;
  }

  static public double l1Norm(RealVector v) {
    if (v instanceof BVector)
      return ((BVector) v).nonZeroElements();
    if (v instanceof SVector) {
      double sum = 0.0;
      SVector sv = (SVector) v;
      for (int i = 0; i < sv.nonZeroElements(); i++)
        sum += Math.abs(sv.values[i]);
      return sum;
    }
    double sum = 0.0;
    for (int i = 0; i < v.getDimension(); i++)
      sum += Math.abs(v.getEntry(i));
    return sum;
  }

  public static MutableVector toBinary(RealVector v, MutableVector result) {
    result.clear();
    if (v instanceof SVector) {
      SVector sv = (SVector) v;
      for (int i = 0; i < sv.nonZeroElements(); i++)
        result.setEntry(sv.activeIndexes[i], 1);
      return result;
    }
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

  public static MutableVector maxToSelf(MutableVector result, RealVector other) {
    for (int i = 0; i < result.getDimension(); i++)
      result.setEntry(i, Math.max(result.getEntry(i), other.getEntry(i)));
    return result;
  }

  public static MutableVector positiveMaxToSelf(MutableVector result, RealVector other) {
    if (other instanceof SVector)
      return positiveMaxToSelf(result, (SVector) other);
    return maxToSelf(result, other);
  }

  private static MutableVector positiveMaxToSelf(MutableVector result, SVector sother) {
    for (int position = 0; position < sother.nonZeroElements(); position++) {
      final int index = sother.activeIndexes[position];
      result.setEntry(index, Math.max(result.getEntry(index), sother.values[position]));
    }
    return result;
  }

  public static MutableVector mapMultiplyToSelf(PVector v, double d, RealVector mask) {
    if (!(mask instanceof SparseVector))
      return v.mapMultiplyToSelf(d);
    SparseVector svector = (SparseVector) mask;
    int nbActive = svector.nonZeroElements();
    int[] actives = svector.nonZeroIndexes();
    for (int i = 0; i < nbActive; i++)
      v.data[actives[i]] *= d;
    return v;
  }
}
