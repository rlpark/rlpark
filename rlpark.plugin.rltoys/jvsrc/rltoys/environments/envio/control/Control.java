package rltoys.environments.envio.control;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public interface Control extends Serializable {
  Action proposeAction(RealVector x);
}
