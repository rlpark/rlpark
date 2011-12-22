package rltoys.algorithms.learning.control.qlearning;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.control.ControlLearner;
import rltoys.environments.envio.offpolicy.OffPolicyLearner;
import rltoys.math.vector.RealVector;

public class QLearningControl implements ControlLearner, OffPolicyLearner {
  private static final long serialVersionUID = 5784749108581105369L;
  private final QLearning qlearning;
  private final Policy acting;

  public QLearningControl(Policy acting, QLearning qlearning) {
    this.qlearning = qlearning;
    this.acting = acting;
  }

  @Override
  public Action step(RealVector s_t, Action a_t, RealVector s_tp1, double r_tp1) {
    Action a_tp1 = acting.decide(s_tp1);
    qlearning.update(s_t, a_t, s_tp1, a_tp1, r_tp1);
    return a_tp1;
  }

  @Override
  public void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double r_tp1) {
    qlearning.update(x_t, a_t, x_tp1, a_tp1, r_tp1);
  }

  @Override
  public Action proposeAction(RealVector x) {
    return acting.decide(x);
  }

  public Policy acting() {
    return acting;
  }

  @Override
  public Policy targetPolicy() {
    return acting;
  }

  @Override
  public Predictor predictor() {
    return qlearning;
  }
}
