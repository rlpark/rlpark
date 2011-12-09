package rltoys.experiments.reinforcementlearning.problemtest;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;

public final class TestRLProblemForSweep implements RLProblem {
  TRStep last = null;
  private final Double defaultReward;

  public TestRLProblemForSweep(Double defaultReward) {
    this.defaultReward = defaultReward;
  }

  @Override
  public TRStep step(Action action) {
    double reward = defaultReward == null ? ((ActionArray) action).actions[0] : defaultReward;
    TRStep result = new TRStep(last, action, new double[] {}, reward);
    last = result;
    return result;
  }

  @Override
  public Legend legend() {
    return new Legend();
  }

  @Override
  public TRStep initialize() {
    last = new TRStep(new double[] {}, 1);
    return last;
  }
}