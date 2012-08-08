package rlpark.plugin.rltoys.envio.policy;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public interface Policy extends Serializable {
  void update(RealVector x);

  double pi(Action a);

  Action sampleAction();
}
