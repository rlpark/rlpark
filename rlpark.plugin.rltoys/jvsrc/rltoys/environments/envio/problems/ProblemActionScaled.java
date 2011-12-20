package rltoys.environments.envio.problems;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;
import rltoys.math.ranges.Range;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ProblemActionScaled implements ProblemBounded, ProblemContinuousAction, ProblemDiscreteAction {
  @Monitor
  private final ProblemContinuousAction problem;
  private final double[] means;
  private final double[] scales;

  public ProblemActionScaled(ProblemContinuousAction problem) {
    this.problem = problem;
    Range[] ranges = problem.actionRanges();
    means = new double[ranges.length];
    scales = new double[ranges.length];
    for (int i = 0; i < ranges.length; i++) {
      means[i] = (ranges[i].max() + ranges[i].min()) / 2.0;
      scales[i] = ranges[i].max() - ranges[i].min();
    }
  }

  @Override
  public TRStep initialize() {
    return problem.initialize();
  }

  @Override
  public TRStep step(Action action) {
    if (action == null)
      return problem.step(action);
    ActionArray agentAction = (ActionArray) action;
    double[] scaled = new double[agentAction.actions.length];
    for (int i = 0; i < scaled.length; i++)
      scaled[i] = (agentAction.actions[i] * scales[i]) + means[i];
    TRStep step = problem.step(new ActionArray(scaled));
    return new TRStep(step.time, step.o_t, agentAction, step.o_tp1, step.r_tp1);
  }

  @Override
  public Legend legend() {
    return problem.legend();
  }

  @Override
  public Action[] actions() {
    return ((ProblemDiscreteAction) problem).actions();
  }

  @Override
  public Range[] actionRanges() {
    Range[] ranges = new Range[problem.actionRanges().length];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = new Range(-1, 1);
    return ranges;
  }

  @Override
  public Range[] getObservationRanges() {
    return ((ProblemBounded) problem).getObservationRanges();
  }

  public ProblemContinuousAction problem() {
    return problem;
  }
}
