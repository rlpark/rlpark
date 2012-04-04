package rlpark.plugin.rltoys.algorithms.representations.actions;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface StateToStateAction extends Serializable {
  RealVector stateAction(RealVector s, Action a);

  double vectorNorm();

  int vectorSize();
}
