package rlpark.plugin.rltoys.algorithms.control;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface Control extends Serializable {
  Action proposeAction(RealVector x);
}
