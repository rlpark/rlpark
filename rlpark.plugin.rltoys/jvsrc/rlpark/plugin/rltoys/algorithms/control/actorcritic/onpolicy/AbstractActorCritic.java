package rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy;


import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public abstract class AbstractActorCritic implements ControlLearner {
  private static final long serialVersionUID = -6085810735822394602L;
  public final Actor actor;
  public final OnPolicyTD critic;
  protected double reward = 0.0;
  protected boolean policyRequireUpdate = true;

  public AbstractActorCritic(OnPolicyTD critic, Actor actor) {
    this.critic = critic;
    this.actor = actor;
  }

  abstract protected double updateCritic(RealVector x_t, RealVector x_tp1, double r_tp1);

  abstract protected void updateActor(RealVector x_t, Action a_t, double actorDelta);

  @Override
  public Action proposeAction(RealVector x) {
    policyRequireUpdate = true;
    policy().update(x);
    return policy().sampleAction();
  }

  protected PolicyDistribution policy() {
    return actor.policy();
  }

  public Actor actor() {
    return actor;
  }

  public OnPolicyTD critic() {
    return critic;
  }

  @Override
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    reward = r_tp1;
    double actorDelta = updateCritic(x_t, x_tp1, r_tp1);
    policyRequireUpdate = x_t == null || policyRequireUpdate;
    if (policyRequireUpdate && x_t != null) {
      policy().update(x_t);
      policyRequireUpdate = false;
    }
    updateActor(x_t, a_t, actorDelta);
    policy().update(x_tp1);
    policyRequireUpdate = false;
    return policy().sampleAction();
  }
}