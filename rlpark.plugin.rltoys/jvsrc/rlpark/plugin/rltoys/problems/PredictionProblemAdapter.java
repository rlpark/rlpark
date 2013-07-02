package rlpark.plugin.rltoys.problems;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class PredictionProblemAdapter implements PredictionProblem {
  private final RLProblem problem;
  private final Projector projector;
  private final Action action;
  private TRStep step;

  public PredictionProblemAdapter(RLProblem problem, Projector projector, Action action) {
    this.problem = problem;
    this.projector = projector;
    this.action = action;
    step = problem.initialize();
  }

  @Override
  public int inputDimension() {
    return projector.vectorSize();
  }

  @Override
  public boolean update() {
    assert !step.isEpisodeEnding();
    step = problem.step(action);
    return true;
  }

  @Override
  public double target() {
    return step.r_tp1;
  }

  @Override
  public RealVector input() {
    return projector.project(step.o_t);
  }
}
