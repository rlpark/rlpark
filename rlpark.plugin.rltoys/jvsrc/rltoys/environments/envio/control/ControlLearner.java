package rltoys.environments.envio.control;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public interface ControlLearner extends Control {
  Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1);
}