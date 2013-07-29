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
    final double[] data = (v instanceof SVector) ? ((SVector) v).values : v.accessData();
    final int nbChecks = (v instanceof SVector) ? ((SVector) v).nonZeroElements() : v.getDimension();
    for (int i = 0; i < nbChecks; i++)
      if (!Utils.checkValue(data[i]))
        return false;
    return true;
  }

  static public MutableVector absToSelf(MutableVector v) {
    if (v instanceof SVector) {
      absToSelf(((SVector) v).values, ((SVector) v).nbActive);
      return v;
    }
    if (v instanceof PVector) {
      absToSelf(((PVector) v).data, v.getDimension());
      return v;
    }
    throw new NotImplemented();
  }

  static public void absToSelf(double[] data, int length) {
    for (int i = 0; i < length; i++)
      data[i] = Math.abs(data[i]);
  }

  static public double l1Norm(RealVector v) {
    if (v instanceof BVector)
      return ((BVector) v).nonZeroElements();
    double[] data;
    int length;
    if (v instanceof SVector) {
      SVector sv = (SVector) v;
      data = sv.values;
      length = sv.nonZeroElements();
    } else {
      data = v.accessData();
      length = data.length;
    }
    double sum = 0.0;
    for (int i = 0; i < length; i++)
      sum += Math.abs(data[i]);
    return sum;
  }

  public static double infNorm(RealVector v) {
    if (v instanceof BVector)
      return ((BVector) v).nonZeroElements() > 0 ? 1 : 0;
    double[] data;
    int length;
    if (v instanceof SVector) {
      SVector sv = (SVector) v;
      data = sv.values;
      length = sv.nonZeroElements();
    } else {
      data = v.accessData();
      length = data.length;
    }
    double max = 0.0;
    for (int i = 0; i < length; i++)
      max = Math.max(max, Math.abs(data[i]));
    return max;
  }

  public static MutableVector toBinary(MutableVector result, RealVector v) {
    assert result.getDimension() == v.getDimension();
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
    if (v == null)
      return true;
    if (v instanceof VectorNull)
      return true;
    if (v instanceof SparseVector)
      return ((SparseVector) v).nonZeroElements() == 0;
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

  public static RealVector bufferedCopy(RealVector source, RealVector target) {
    if (source == null) {
      if (target != null)
        if (target instanceof BVector)
          ((BVector) target).clear();
        else
          ((MutableVector) target).clear();
      return target;
    }
    RealVector result = target != null ? target : source.copy();
    if (result instanceof BVector)
      ((BVector) result).set((BVector) source);
    else
      ((MutableVector) result).set(source);
    return result;
  }

  public static MutableVector logToSelf(MutableVector v) {
    if (v instanceof SVector) {
      logToSelf(((SVector) v).values, ((SVector) v).nbActive);
      return v;
    }
    if (v instanceof PVector) {
      logToSelf(((PVector) v).data, v.getDimension());
      return v;
    }
    throw new NotImplemented();
  }

  private static void logToSelf(double[] data, int length) {
    for (int i = 0; i < length; i++)
      data[i] = Math.log(data[i]);
  }
}
