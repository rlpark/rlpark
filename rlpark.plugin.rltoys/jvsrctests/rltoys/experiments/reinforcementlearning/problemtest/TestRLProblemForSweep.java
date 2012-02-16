package rltoys.experiments.reinforcementlearning.problemtest;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;

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
  public TRStep endEpisode() {
    step = step.createEndingStep();
    return step;
  }
}