package rlpark.plugin.rltoys.math.vector;

public interface SparseVector extends RealVector {
  void clear();

  double dotProduct(double[] data);

  void addSelfTo(double[] data);

  void subtractSelfTo(double[] data);

  int nonZeroElements();
}
