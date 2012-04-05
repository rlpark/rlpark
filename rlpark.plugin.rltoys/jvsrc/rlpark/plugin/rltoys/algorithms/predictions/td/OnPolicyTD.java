package rlpark.plugin.rltoys.algorithms.predictions.td;


import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface OnPolicyTD extends Predictor, LinearLearner {
  double update(RealVector x_t, RealVector x_tp1, double r_tp1);

  double prediction();
}
