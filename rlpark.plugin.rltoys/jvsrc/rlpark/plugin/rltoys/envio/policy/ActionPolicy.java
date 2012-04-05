package rlpark.plugin.rltoys.envio.policy;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class ActionPolicy implements Policy {
  private static final long serialVersionUID = -1014952467366264062L;
  private final Action action;

  public ActionPolicy(Action action) {
    this.action = action;
  }

  @Override
  public double pi(RealVector s, Action a) {
    return a == action ? 1.0 : 0.0;
  }

  @Override
  public Action decide(RealVector s) {
    return action;
  }
}
