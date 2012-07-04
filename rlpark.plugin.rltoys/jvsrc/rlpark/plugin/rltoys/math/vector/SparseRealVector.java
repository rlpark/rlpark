package rlpark.plugin.rltoys.math.vector;

public interface SparseRealVector extends MutableVector, SparseVector {
  @Override
  SparseRealVector clear();

  void removeEntry(int index);
}
