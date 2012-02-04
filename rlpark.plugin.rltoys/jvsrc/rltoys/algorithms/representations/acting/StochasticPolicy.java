package rltoys.algorithms.representations.acting;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public abstract class StochasticPolicy implements Policy, MonitorContainer {
  private static final long serialVersionUID = 6747532059495537542L;
  protected final Random random;
  protected final Action[] actions;
  protected final Map<Action, Integer> actionToIndex = new LinkedHashMap<Action, Integer>();

  public StochasticPolicy(Random random, Action[] actions) {
    this.random = random;
    this.actions = actions;
    for (int i = 0; i < actions.length; i++)
      actionToIndex.put(actions[i], i);
  }

  protected int atoi(Action a) {
    return actionToIndex.get(a);
  }

  protected Action chooseAction(double[] distribution) {
    assert checkDistribution(distribution);
    double randomValue = random.nextDouble();
    double sum = 0;
    for (int i = 0; i < distribution.length - 1; i++) {
      sum += distribution[i];
      if (sum >= randomValue)
        return actions[i];
    }
    return actions[actions.length - 1];
  }

  public static boolean checkDistribution(double[] distribution) {
    double sum = 0.0;
    for (double value : distribution)
      sum += value;
    return Math.abs(1.0 - sum) < Utils.EPSILON;
  }

  public Action[] actions() {
    return actions;
  }

  public abstract double[] distribution();

  @Override
  public void addToMonitor(DataMonitor monitor) {
    for (int i = 0; i < actions.length; i++) {
      final int a_i = i;
      monitor.add(Labels.label(actions[i]), new Monitored() {
        @Override
        public double monitoredValue() {
          return distribution()[a_i];
        }
      });
    }
  }
}
