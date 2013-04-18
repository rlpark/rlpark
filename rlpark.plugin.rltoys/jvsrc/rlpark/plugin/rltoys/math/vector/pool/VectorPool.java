package rlpark.plugin.rltoys.math.vector.pool;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;


public interface VectorPool {
  MutableVector newVector();

  MutableVector newVector(RealVector v);

  void releaseAll();
}
