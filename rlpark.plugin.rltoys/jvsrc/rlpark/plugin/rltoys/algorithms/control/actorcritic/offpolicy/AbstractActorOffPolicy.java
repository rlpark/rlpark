package rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public abstract class AbstractActorOffPolicy implements ActorOffPolicy {
  @Monitor
  final protected PolicyDistribution policyDistribution;

  protected AbstractActorOffPolicy(PolicyDistribution policyDistribution) {
    this.policyDistribution = policyDistribution;
  }

  @Override
  public Action proposeAction(RealVector x) {
    return Policies.decide(policyDistribution, x);
  }


  @Override
  public void update(double rho_t, RealVector x_t, Action a_t, double delta) {
    if (x_t == null) {
      initEpisode();
      return;
    }
    updateParameters(rho_t, x_t, a_t, delta);
  }

  @Override
  public PolicyDistribution policy() {
    return policyDistribution;
  }

  abstract protected void initEpisode();

  abstract protected void updateParameters(double rho_t, RealVector x_t, Action a_t, double delta);
}
