package rlpark.plugin.rltoys.algorithms.learning.predictions;

import java.io.Serializable;

import rlpark.plugin.rltoys.math.vector.RealVector;

public interface Predictor extends Serializable {
  double predict(RealVector x);
}
