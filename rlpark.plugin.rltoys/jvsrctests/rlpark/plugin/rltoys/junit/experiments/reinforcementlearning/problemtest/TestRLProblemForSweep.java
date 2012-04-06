package rlpark.plugin.rltoys.junit.experiments.reinforcementlearning.problemtest;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.problems.RLProblem;

public final class TestRLProblemForSweep implements RLProblem {
  TRStep step = null;
  private final Double defaultReward;

  public TestRLProblemForSweep(Double defaultReward) {
    this.defaultReward = defaultReward;
  }

  @Override
  public TRStep step(Action action) {
    double reward = defaultReward == null ? ActionArray.toDouble(action) : defaultReward;
    TRStep result = new TRStep(step, action, new double[] {}, reward);
    step = result;
    return result;
  }

  @Override
  public Legend legend() {
    return new Legend();
  }

  @Override
  public TRStep initialize() {
    step = new TRStep(new double[] {}, 1);
    return step;
  }

  @Override
  public TRStep lastStep() {
    return step;
  }

  @Override
  public TRStep forceEndEpisode() {
    step = step.createEndingStep();
    return step;
  }
}