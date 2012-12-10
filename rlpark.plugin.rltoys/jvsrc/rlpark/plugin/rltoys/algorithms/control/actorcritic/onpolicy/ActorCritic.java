package rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy;

import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class ActorCritic extends AbstractActorCritic {
  private static final long serialVersionUID = 3772938582043052714L;

  public ActorCritic(OnPolicyTD critic, Actor actor) {
    super(critic, actor);
  }

  @Override
  protected double updateCritic(RealVector x_t, RealVector x_tp1, double r_tp1) {
    return critic.update(x_t, x_tp1, r_tp1);
  }
}
