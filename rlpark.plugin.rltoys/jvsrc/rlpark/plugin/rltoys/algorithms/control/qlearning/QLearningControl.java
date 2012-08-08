package rlpark.plugin.rltoys.algorithms.control.qlearning;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.control.OffPolicyLearner;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class QLearningControl implements ControlLearner, OffPolicyLearner {
  private static final long serialVersionUID = 5784749108581105369L;
  private final QLearning qlearning;
  private final Policy acting;

  public QLearningControl(Policy acting, QLearning qlearning) {
    this.qlearning = qlearning;
    this.acting = acting;
  }

  @Override
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    Action a_tp1 = Policies.decide(acting, x_tp1);
    qlearning.update(x_t, a_t, x_tp1, a_tp1, r_tp1);
    return a_tp1;
  }

  @Override
  public void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double r_tp1) {
    qlearning.update(x_t, a_t, x_tp1, a_tp1, r_tp1);
  }

  @Override
  public Action proposeAction(RealVector x) {
    return Policies.decide(acting, x);
  }

  public Policy acting() {
    return acting;
  }

  @Override
  public Policy targetPolicy() {
    return acting;
  }

  @Override
  public QLearning predictor() {
    return qlearning;
  }
}
