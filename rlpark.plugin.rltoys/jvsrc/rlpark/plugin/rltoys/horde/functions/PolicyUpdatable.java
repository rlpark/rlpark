package rlpark.plugin.rltoys.horde.functions;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Observation;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class PolicyUpdatable implements HordeUpdatable {
  private final Policy policy;

  public PolicyUpdatable(Policy policy) {
    this.policy = policy;
  }

  @Override
  public void update(Observation o_tp1, RealVector x_t, Action a_t, RealVector x_tp1) {
    policy.update(x_t);
  }

  public Policy policy() {
    return policy;
  }
}
