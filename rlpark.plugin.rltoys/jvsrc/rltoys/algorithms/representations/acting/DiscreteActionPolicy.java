package rltoys.algorithms.representations.acting;

import rltoys.algorithms.representations.actions.Action;

public interface DiscreteActionPolicy extends Policy {
  double[] values();

  Action[] actions();
}
