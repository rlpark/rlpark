package rlpark.plugin.rltoys.algorithms.representations.discretizer;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.actions.Action;

public interface ActionDiscretizer extends Serializable {
  double[] discretize(Action action);

  Discretizer[] actionDiscretizers();

  int nbOutput();
}
