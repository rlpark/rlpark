package rlpark.plugin.rltoys.envio.policy;

import rlpark.plugin.rltoys.envio.actions.Action;

public interface DiscreteActionPolicy extends Policy {
  double[] values();

  Action[] actions();
}
