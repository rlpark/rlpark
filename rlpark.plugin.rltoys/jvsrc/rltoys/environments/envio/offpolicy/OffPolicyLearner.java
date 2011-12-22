package rltoys.environments.envio.offpolicy;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.control.Control;
import rltoys.math.vector.RealVector;

public interface OffPolicyLearner extends Control {
  void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double reward);

  Policy targetPolicy();

  Predictor predictor();
}
