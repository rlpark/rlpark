package rlpark.plugin.rltoys.algorithms.representations.discretizer;

import java.io.Serializable;

public interface Discretizer extends Serializable {
  int discretize(double input);

  int resolution();
}
