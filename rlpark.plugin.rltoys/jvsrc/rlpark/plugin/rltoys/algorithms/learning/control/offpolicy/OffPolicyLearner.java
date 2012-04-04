package rlpark.plugin.rltoys.algorithms.learning.control.offpolicy;

import rlpark.plugin.rltoys.algorithms.learning.predictions.Predictor;
import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.control.Control;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface OffPolicyLearner extends Control {
  void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double reward);

  Policy targetPolicy();

  Predictor predictor();
}
