package rlpark.plugin.rltoys.math.vector.implementations;

import rlpark.plugin.rltoys.math.vector.RealVector;


public class PVectors {
  static public double mean(PVector vector) {
    double[] a = vector.data;
    double sum = 0.0;
    for (int i = 0; i < vector.size; i++)
      sum += a[i];
    return sum / vector.size;
  }

  // TODO: Untested
  static public double min(PVector vector) {
    double min = Double.MAX_VALUE;
    for (double value : vector.data)
      min = Math.min(value, min);
    return min;
  }

  // TODO: Untested
  static public double max(PVector vector) {
    double max = -Double.MAX_VALUE;
    for (double value : vector.data)
      max = Math.max(value, max);
    return max;
  }

  // TODO: Untested
  public static SVector toSVector(PVector v) {
    SVector result = new SVector(v.getDimension());
    for (int i = 0; i < v.size; i++) {
      final double value = v.data[i];
      if (value != 0)
        result.setEntry(i, value);
    }
    return result;
  }

  public static PVector multiplySelfByExponential(PVector result, RealVector other) {
    if (other instanceof SVector)
      return multiplySelfByExponential(result, (SVector) other);
    for (int i = 0; i < result.size; i++)
      result.data[i] *= Math.exp(other.getEntry(i));
    return result;
  }

  public static PVector multiplySelfByExponential(PVector result, SVector other) {
    int[] activeIndexes = other.activeIndexes();
    for (int i = 0; i < activeIndexes.length; i++) {
      int index = activeIndexes[i];
      result.data[index] *= Math.exp(other.values[i]);
    }
    return result;
  }
}
