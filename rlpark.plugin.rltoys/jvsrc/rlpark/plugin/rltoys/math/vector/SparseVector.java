package rlpark.plugin.rltoys.math.vector;

public interface SparseVector extends RealVector {
  RealVector clear();

  double dotProduct(double[] data);

  void addSelfTo(double[] data);

  void subtractSelfTo(double[] data);

  int nonZeroElements();

  int[] nonZeroIndexes();
}
