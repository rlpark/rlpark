package rlpark.plugin.rltoys.algorithms.predictions.supervised;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface LearningAlgorithm extends Predictor {
  double learn(RealVector x_t, double y_tp1);
}
