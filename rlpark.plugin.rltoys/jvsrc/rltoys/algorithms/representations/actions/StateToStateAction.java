package rltoys.algorithms.representations.actions;

import java.io.Serializable;

import rltoys.math.vector.RealVector;

public interface StateToStateAction extends Serializable {
  RealVector stateAction(RealVector s, Action a);

  double vectorNorm();

  int vectorSize();
}
