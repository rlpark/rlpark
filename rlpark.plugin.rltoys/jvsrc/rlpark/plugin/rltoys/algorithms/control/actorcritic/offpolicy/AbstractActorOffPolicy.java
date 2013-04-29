package rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public abstract class AbstractActorOffPolicy implements ActorOffPolicy {
  @Monitor
  final protected PolicyDistribution targetPolicy;
  @Monitor(level = 4)
  final protected PVector[] u;

  protected AbstractActorOffPolicy(PVector[] policyParameters, PolicyDistribution policyDistribution) {
    u = policyParameters;
    this.targetPolicy = policyDistribution;
  }

  @Override
  public Action proposeAction(RealVector x) {
    return Policies.decide(targetPolicy, x);
  }

  @Override
  public void update(double pi_t, double b_t, RealVector x_t, Action a_t, double delta) {
    if (x_t == null) {
      initEpisode();
      return;
    }
    updateParameters(pi_t, b_t, x_t, a_t, delta);
  }

  @Override
  public PolicyDistribution policy() {
    return targetPolicy;
  }

  @Override
  public PVector[] actorParameters() {
    return u;
  }

  abstract protected void initEpisode();

  abstract protected void updateParameters(double pi_t, double b_t, RealVector x_t, Action a_t, double delta);
}
