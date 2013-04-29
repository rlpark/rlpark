package rlpark.plugin.rltoys.algorithms.predictions.td;


import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

public interface OffPolicyTD extends Predictor, LinearLearner {
  double update(double pi_t, double b_t, RealVector x_t, RealVector x_tp1, double r_tp1);

  double prediction();

  PVector secondaryWeights();
}
