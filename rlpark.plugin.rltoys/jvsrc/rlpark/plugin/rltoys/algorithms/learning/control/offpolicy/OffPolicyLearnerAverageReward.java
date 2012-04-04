package rlpark.plugin.rltoys.algorithms.learning.control.offpolicy;

import rlpark.plugin.rltoys.algorithms.learning.predictions.Predictor;
import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class OffPolicyLearnerAverageReward implements OffPolicyLearner {
  private static final long serialVersionUID = -6411746000894511629L;
  private final OffPolicyAverageReward averageReward;
  private final OffPolicyLearner control;

  public OffPolicyLearnerAverageReward(OffPolicyAverageReward averageReward, OffPolicyLearner control) {
    this.averageReward = averageReward;
    this.control = control;
  }

  @Override
  public void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double r_tp1) {
    control.learn(x_t, a_t, x_tp1, a_tp1, averageReward.average(x_t, a_t, r_tp1));
  }

  @Override
  public Action proposeAction(RealVector x_t) {
    return control.proposeAction(x_t);
  }

  @Override
  public Policy targetPolicy() {
    return control.targetPolicy();
  }

  @Override
  public Predictor predictor() {
    return control.predictor();
  }
}