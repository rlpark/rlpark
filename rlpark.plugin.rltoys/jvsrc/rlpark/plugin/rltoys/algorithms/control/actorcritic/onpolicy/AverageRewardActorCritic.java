package rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy;

import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class AverageRewardActorCritic extends AbstractActorCritic {
  private static final long serialVersionUID = 3772938582043052714L;
  protected double averageReward = 0.0;
  private final double alpha_r;

  public AverageRewardActorCritic(double alpha_r, OnPolicyTD critic, Actor actor) {
    super(critic, actor);
    this.alpha_r = alpha_r;
  }

  @Override
  protected double updateCritic(RealVector x_t, RealVector x_tp1, double r_tp1) {
    double delta = critic.update(x_t, x_tp1, r_tp1 - averageReward);
    averageReward += alpha_r * delta;
    return delta;
  }

  @Override
  protected void updateActor(RealVector x_t, Action a_t, double actorDelta) {
    actor.update(x_t, a_t, actorDelta);
  }

  public double currentAverage() {
    return averageReward;
  }
}
