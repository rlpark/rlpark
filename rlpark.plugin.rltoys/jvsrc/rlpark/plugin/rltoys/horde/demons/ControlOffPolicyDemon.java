package rlpark.plugin.rltoys.horde.demons;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.algorithms.control.gq.GreedyGQ;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.horde.functions.ConstantGamma;
import rlpark.plugin.rltoys.horde.functions.ConstantOutcomeFunction;
import rlpark.plugin.rltoys.horde.functions.GammaFunction;
import rlpark.plugin.rltoys.horde.functions.OutcomeFunction;
import rlpark.plugin.rltoys.horde.functions.RewardFunction;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ControlOffPolicyDemon implements Demon {
  private static final long serialVersionUID = -7997723890930214800L;
  private final RewardFunction rewardFunction;
  private final OutcomeFunction outcomeFunction;
  @Monitor
  private final GreedyGQ gq;
  private final GammaFunction gammaFunction;

  public ControlOffPolicyDemon(RewardFunction rewardFunction, final GreedyGQ gq) {
    this(gq, rewardFunction, new ConstantGamma(gq.gamma()), new ConstantOutcomeFunction(0));
  }

  public ControlOffPolicyDemon(GreedyGQ gq, RewardFunction rewardFunction, GammaFunction gammaFunction,
      OutcomeFunction outcomeFunction) {
    this.rewardFunction = rewardFunction;
    this.gq = gq;
    this.outcomeFunction = outcomeFunction;
    this.gammaFunction = gammaFunction;
  }

  @Override
  public void update(RealVector x_t, Action a_t, RealVector x_tp1) {
    gq.update(x_t, a_t, rewardFunction.reward(), gammaFunction.gamma(), outcomeFunction.outcome(), x_tp1, a_t);
  }

  public RewardFunction rewardFunction() {
    return rewardFunction;
  }

  public OutcomeFunction outcomeFunction() {
    return outcomeFunction;
  }

  public Predictor predictor() {
    return gq.predictor();
  }

  public Policy targetPolicy() {
    return gq.targetPolicy();
  }

  @Override
  public LinearLearner learner() {
    return gq.gq();
  }
}
