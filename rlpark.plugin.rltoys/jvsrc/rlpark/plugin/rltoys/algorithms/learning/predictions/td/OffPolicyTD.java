package rlpark.plugin.rltoys.algorithms.learning.predictions.td;


import rlpark.plugin.rltoys.algorithms.learning.predictions.LinearLearner;
import rlpark.plugin.rltoys.algorithms.learning.predictions.Predictor;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public interface OffPolicyTD extends Predictor, LinearLearner {
  double update(double rho_t, RealVector x_t, RealVector x_tp1, double r_tp1);

  double prediction();

  PVector secondaryWeights();
}
