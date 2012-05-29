package rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class AverageRewardActorCritic implements ControlLearner {
  private static final long serialVersionUID = 3772938582043052714L;
  public final OnPolicyTD critic;
  public final Actor actor;
  protected Action lastAction = null;
  protected double averageReward = 0.0;
  private final double alpha_r;

  public AverageRewardActorCritic(double alpha_r, OnPolicyTD critic, Actor actor) {
    this.critic = critic;
    this.actor = actor;
    this.alpha_r = alpha_r;
  }

  protected double updateCritic(RealVector x_t, RealVector x_tp1, double r_tp1) {
    return critic.update(x_t, x_tp1, r_tp1);
  }

  protected void updateActor(RealVector x_t, double delta) {
    actor.update(x_t, lastAction, delta);
  }

  protected Action computeAction(RealVector x_tp1) {
    lastAction = x_tp1 != null ? actor.proposeAction(x_tp1) : null;
    return x_tp1 != null ? ActionArray.merge(lastAction) : null;
  }

  @Override
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    double delta = updateCritic(x_t, x_tp1, r_tp1 - averageReward);
    averageReward += alpha_r * delta;
    updateActor(x_t, delta);
    return computeAction(x_tp1);
  }

  @Override
  public Action proposeAction(RealVector x) {
    return computeAction(x);
  }

  public Actor actor() {
    return actor;
  }

  public double currentAverage() {
    return averageReward;
  }
}
