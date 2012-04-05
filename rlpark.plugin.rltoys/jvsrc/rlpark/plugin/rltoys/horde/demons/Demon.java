package rlpark.plugin.rltoys.horde.demons;

import java.io.Serializable;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface Demon extends Serializable {
  void update(RealVector x_t, Action a_t, RealVector x_tp1);

  LinearLearner learner();
}
