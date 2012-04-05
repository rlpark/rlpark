package rlpark.plugin.rltoys.horde.demons;

import rlpark.plugin.rltoys.algorithms.LinearLearner;
import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.horde.functions.RewardFunction;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class PredictionDemon implements Demon, Labeled {
  private static final long serialVersionUID = -6966208035134604865L;
  private final RewardFunction rewardFunction;
  @Monitor
  private final OnPolicyTD td;

  public PredictionDemon(RewardFunction rewardFunction, OnPolicyTD td) {
    this.rewardFunction = rewardFunction;
    this.td = td;
  }

  @Override
  public void update(RealVector x_t, Action a_t, RealVector x_tp1) {
    td.update(x_t, x_tp1, rewardFunction.reward());
  }

  public double prediction() {
    return td.prediction();
  }

  public RewardFunction rewardFunction() {
    return rewardFunction;
  }

  public OnPolicyTD predicter() {
    return td;
  }

  @Override
  public String label() {
    return "demon" + Labels.label(rewardFunction);
  }

  @Override
  public LinearLearner learner() {
    return td;
  }
}
