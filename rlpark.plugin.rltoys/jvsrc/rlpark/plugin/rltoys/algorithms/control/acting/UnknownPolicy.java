package rlpark.plugin.rltoys.algorithms.control.acting;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class UnknownPolicy implements Policy {
  private static final long serialVersionUID = -4805473070123975706L;
  private final Policy policy;

  public UnknownPolicy(Policy policy) {
    this.policy = policy;
  }

  @Override
  public double pi(Action a) {
    return 1.0;
  }

  @Override
  public Action sampleAction() {
    return policy.sampleAction();
  }

  @Override
  public void update(RealVector x) {
    policy.update(x);
  }
}
