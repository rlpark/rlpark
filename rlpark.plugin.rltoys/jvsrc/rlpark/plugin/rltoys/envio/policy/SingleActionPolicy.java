package rlpark.plugin.rltoys.envio.policy;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class SingleActionPolicy implements Policy {
  private static final long serialVersionUID = -1014952467366264062L;
  private final Action action;

  public SingleActionPolicy(Action action) {
    this.action = action;
  }

  @Override
  public double pi(Action a) {
    return a == action ? 1.0 : 0.0;
  }

  @Override
  public Action sampleAction() {
    return action;
  }

  @Override
  public void update(RealVector x) {
  }
}
