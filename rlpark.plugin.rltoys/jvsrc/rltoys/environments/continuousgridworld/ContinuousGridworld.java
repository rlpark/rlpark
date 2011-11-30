package rltoys.environments.continuousgridworld;

import java.util.Arrays;
import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.ProblemBounded;
import rltoys.environments.envio.problems.ProblemContinuousAction;
import rltoys.math.ranges.Range;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class ContinuousGridworld implements ProblemBounded, ProblemContinuousAction, MonitorContainer {
  static private final Range SpaceRange = new Range(-50, 50);
  static private final Range ActionRange = new Range(-1, 1);
  static private final double Noise = .1;
  protected TRStep step = null;
  private final int nbDimension;
  private final double[] target;
  private final double[] start;
  private final ContinuousFunction rewardFunction;
  private final Legend legend;
  private final Random random;

  public ContinuousGridworld(Random random, double[] start, double[] target, ContinuousFunction rewardFunction) {
    this.random = random;
    nbDimension = start.length;
    this.rewardFunction = rewardFunction != null ? rewardFunction : new ConstantFunction(nbDimension, 0);
    this.start = start;
    this.target = target;
    legend = createLegend();
  }

  private Legend createLegend() {
    String[] labels = new String[nbDimension];
    for (int i = 0; i < nbDimension; i++)
      labels[i] = "x" + i;
    return new Legend(labels);
  }

  @Override
  public TRStep initialize() {
    step = new TRStep(start, rewardFunction.fun(start));
    return step;
  }

  @Override
  public TRStep step(Action action) {
    double[] envAction = computeEnvironmentAction(action);
    double[] x_tp1 = new double[nbDimension];
    for (int i = 0; i < x_tp1.length; i++)
      x_tp1[i] = SpaceRange.bound(step.o_tp1[i] + envAction[i]);
    step = new TRStep(step, action, x_tp1, rewardFunction.fun(x_tp1));
    if (isTargetReached())
      step = step.createEndingStep();
    return step;
  }

  private boolean isTargetReached() {
    if (target == null)
      return false;
    double distance = 0.0;
    for (int i = 0; i < nbDimension; i++) {
      double diff = target[i] - step.o_tp1[i];
      distance += diff * diff;
    }
    distance = Math.sqrt(distance);
    return (distance < ActionRange.max() + 2 * Noise);
  }

  private double[] computeEnvironmentAction(Action action) {
    double[] agentAction = ((ActionArray) action).actions;
    double[] envAction = new double[agentAction.length];
    for (int i = 0; i < envAction.length; i++) {
      double noise = (random.nextDouble() * Noise) - (Noise / 2);
      envAction[i] = ActionRange.bound(agentAction[i]) + noise;
    }
    return envAction;
  }

  @Override
  public Legend legend() {
    return legend;
  }

  @Override
  public Range[] actionRanges() {
    Range[] ranges = new Range[nbDimension];
    Arrays.fill(ranges, ActionRange);
    return ranges;
  }

  @Override
  public Range[] getObservationRanges() {
    Range[] ranges = new Range[nbDimension];
    Arrays.fill(ranges, SpaceRange);
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
}
