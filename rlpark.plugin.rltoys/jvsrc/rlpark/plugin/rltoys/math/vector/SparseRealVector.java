package rlpark.plugin.rltoys.math.vector;

public interface SparseRealVector extends MutableVector, SparseVector {
  void removeEntry(int index);
}
