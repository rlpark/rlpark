package rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy;

import rlpark.plugin.rltoys.algorithms.control.OffPolicyLearner;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.predictions.td.OffPolicyTD;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


@Monitor
public class OffPAC implements OffPolicyLearner {
  private static final long serialVersionUID = -3586849056133550941L;
  public final Policy behavior;
  public final OffPolicyTD critic;
  public final ActorOffPolicy actor;
  protected double rho_t;

  public OffPAC(Policy behavior, OffPolicyTD critic, ActorOffPolicy actor) {
    this.critic = critic;
    this.actor = actor;
    this.behavior = behavior;
  }

  @Override
  public void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double r_tp1) {
    actor.policy().update(x_t);
    behavior.update(x_t);
    rho_t = x_t != null ? computeRho(a_t) : 0.0;
    assert Utils.checkValue(rho_t);
    double delta = critic.update(rho_t, x_t, x_tp1, r_tp1);
    assert Utils.checkValue(delta);
    actor.update(rho_t, x_t, a_t, delta);
  }

  protected double computeRho(Action a_t) {
    return actor.policy().pi(a_t) / behavior.pi(a_t);
  }

  @Override
  public Action proposeAction(RealVector s) {
    final Action action = actor.proposeAction(s);
    assert action != null;
    return action;
  }

  @Override
  public Policy targetPolicy() {
    return actor.policy();
  }

  @Override
  public Predictor predictor() {
    return critic;
  }

  public double rho() {
    return rho_t;
  }
}
