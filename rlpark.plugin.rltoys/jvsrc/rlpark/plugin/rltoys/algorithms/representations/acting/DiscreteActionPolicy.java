package rlpark.plugin.rltoys.algorithms.representations.acting;

import rlpark.plugin.rltoys.envio.actions.Action;

public interface DiscreteActionPolicy extends Policy {
  double[] values();

  Action[] actions();
}
