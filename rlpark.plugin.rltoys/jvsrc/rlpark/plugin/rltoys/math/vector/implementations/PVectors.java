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

  public static PVector multiplySelfByExponential(PVector result, double factor, RealVector other) {
    return multiplySelfByExponential(result, factor, other, 0);
  }

  public static PVector multiplySelfByExponential(PVector result, double factor, RealVector other, double min) {
    if (other instanceof SVector)
      return multiplySelfByExponential(result, factor, (SVector) other, min);
    for (int i = 0; i < result.size; i++)
      result.data[i] = Math.max(min, result.data[i] * Math.exp(factor * other.getEntry(i)));
    return result;
  }

  public static PVector multiplySelfByExponential(PVector result, double factor, SVector other, double min) {
    int[] activeIndexes = other.getActiveIndexes();
    for (int i = 0; i < other.nonZeroElements(); i++) {
      int index = activeIndexes[i];
      result.data[index] = Math.max(min, result.data[index] * Math.exp(factor * other.values[i]));
    }
    return result;
  }
}
