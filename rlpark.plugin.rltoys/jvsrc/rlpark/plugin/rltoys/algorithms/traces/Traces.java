package rlpark.plugin.rltoys.algorithms.traces;

import java.io.Serializable;

import rlpark.plugin.rltoys.math.vector.MutableVector;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface Traces extends Serializable {
  Traces newTraces(int size);

  void update(double lambda, RealVector phi);

  void clear();

  MutableVector vect();
}
