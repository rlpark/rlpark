package rlpark.plugin.rltoys.algorithms.control.acting;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class ControlPolicyAdapter implements PolicyBasedControl {
  private static final long serialVersionUID = 7405967970830537947L;
  private final Policy policy;

  public ControlPolicyAdapter(Policy policy) {
    this.policy = policy;
  }

  @Override
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    return Policies.decide(policy, x_tp1);
  }

  @Override
  public Action proposeAction(RealVector x) {
    return Policies.decide(policy, x);
  }

  @Override
  public Policy policy() {
    return policy;
  }
}
