package rlpark.plugin.rltoys.algorithms.learning.predictions;

import rlpark.plugin.rltoys.math.vector.RealVector;

public interface LearningAlgorithm extends Predictor {
  double learn(RealVector x, double y);
}
