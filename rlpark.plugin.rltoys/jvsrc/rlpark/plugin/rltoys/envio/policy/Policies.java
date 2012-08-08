package rlpark.plugin.rltoys.envio.policy;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class Policies {
  public static Action decide(Policy policy, RealVector x) {
    policy.update(x);
    return policy.sampleAction();
  }
}
