package rlpark.plugin.rltoys.algorithms.control;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface ControlLearner extends Control {
  Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1);
}