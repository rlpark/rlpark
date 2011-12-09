package rltoys.environments.continuousgridworld;

import java.util.Arrays;
import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.ProblemBounded;
import rltoys.environments.envio.problems.ProblemContinuousAction;
import rltoys.environments.envio.problems.ProblemDiscreteAction;
import rltoys.math.ranges.Range;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class ContinuousGridworld implements ProblemBounded, ProblemDiscreteAction, ProblemContinuousAction,
    MonitorContainer {
  private final Action[] actions;
  protected TRStep step = null;
  private final int nbDimensions;
  private double[] start = null;
  private ContinuousFunction rewardFunction = null;
  private final Legend legend;
  private final Random random;
  private TerminationFunction terminationFunction = null;
  private final Range observationRange;
  private final Range actionRange;
  private final double absoluteNoise;

  public ContinuousGridworld(Random random, int nbDimension, Range observationRange, Range actionRange,
      double relativeNoise) {
    this.random = random;
    this.observationRange = observationRange;
    this.actionRange = actionRange;
    this.nbDimensions = nbDimension;
    this.absoluteNoise = (actionRange.length() / 2.0) * relativeNoise;
    legend = createLegend();
    actions = createActions();
  }

  private Action[] createActions() {
    Action[] actions = new Action[2 * nbDimensions + 1];
    for (int i = 0; i < actions.length - 1; i++) {
      int dimension = i / 2;
      int dimensionAction = i % 2;
      double[] actionValues = Utils.newFilledArray(nbDimensions, 0);
      if (dimensionAction == 0)
        actionValues[dimension] = -1;
      else
        actionValues[dimension] = 1;
      actions[i] = new ActionArray(actionValues);
    }
    actions[actions.length - 1] = new ActionArray(Utils.newFilledArray(nbDimensions, 0));
    return actions;
  }

  public void setStart(double[] start) {
    this.start = start;
  }

  public void setRewardFunction(ContinuousFunction rewardFunction) {
    this.rewardFunction = rewardFunction;
  }

  public void setTermination(TerminationFunction terminationFunction) {
    this.terminationFunction = terminationFunction;
  }

  private Legend createLegend() {
    String[] labels = new String[nbDimensions];
    for (int i = 0; i < nbDimensions; i++)
      labels[i] = "x" + i;
    return new Legend(labels);
  }

  @Override
  public TRStep initialize() {
    double[] position = start;
    if (position == null) {
      position = new double[nbDimensions];
      for (int i = 0; i < position.length; i++)
        position[i] = observationRange.choose(random);
    }
    step = new TRStep(position, reward(position));
    return step;
  }

  public double[] currentPosition() {
    if (step == null || step.isEpisodeEnding())
      return null;
    return step.o_tp1;
  }

  @Override
  public TRStep step(Action action) {
    double[] envAction = computeEnvironmentAction(action);
    double[] x_tp1 = new double[nbDimensions];
    for (int i = 0; i < x_tp1.length; i++)
      x_tp1[i] = observationRange.bound(step.o_tp1[i] + envAction[i]);
    step = new TRStep(step, action, x_tp1, reward(x_tp1));
    if (isTerminated(x_tp1))
      step = step.createEndingStep();
    return step;
  }

  private double reward(double[] position) {
    if (rewardFunction == null)
      return 0.0;
    return rewardFunction.fun(position);
  }

  private boolean isTerminated(double[] position) {
    if (terminationFunction == null)
      return false;
    return terminationFunction.isTerminated(position);
  }

  private double[] computeEnvironmentAction(Action action) {
    double[] agentAction = ((ActionArray) action).actions;
    double[] envAction = new double[agentAction.length];
    for (int i = 0; i < envAction.length; i++) {
      double noise = (random.nextDouble() * absoluteNoise) - (absoluteNoise / 2);
      envAction[i] = actionRange.bound(agentAction[i]) + noise;
    }
    return envAction;
  }

  @Override
  public Legend legend() {
    return legend;
  }

  @Override
  public Range[] actionRanges() {
    Range[] ranges = new Range[nbDimensions];
    Arrays.fill(ranges, actionRange);
    return ranges;
  }

  @Override
  public Range[] getObservationRanges() {
    Range[] ranges = new Range[nbDimensions];
    Arrays.fill(ranges, observationRange);
    return ranges;
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    monitor.add("Reward", 0, new Monitored() {
      @Override
      public double monitoredValue() {
        return step != null ? step.r_tp1 : 0.0;
      }
    });
    for (int i = 0; i < legend.nbLabels(); i++) {
      final int index = i;
      monitor.add(legend.label(i), 0, new Monitored() {
        @Override
        public double monitoredValue() {
          return step != null && step.o_tp1 != null ? step.o_tp1[index] : 0.0;
        }
      });
    }
  }

  public int nbDimensions() {
    return nbDimensions;
  }

  public ContinuousFunction rewardFunction() {
    return rewardFunction;
  }

  public double[] start() {
    return start;
  }

  @Override
  public Action[] actions() {
    return actions;
  }
}
