package rltoys.algorithms.representations.discretizer;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;

public interface ActionDiscretizer extends Serializable {
  double[] discretize(Action action);

  Discretizer[] actionDiscretizers();

  int nbOutput();
}
