package rlpark.plugin.rltoys.algorithms.control;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface OffPolicyLearner extends Control {
  void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double reward);

  Policy targetPolicy();

  Predictor predictor();
}
