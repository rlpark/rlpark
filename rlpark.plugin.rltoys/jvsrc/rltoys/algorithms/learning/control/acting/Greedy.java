package rltoys.algorithms.learning.control.acting;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.DiscreteActionPolicy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class Greedy implements DiscreteActionPolicy {
  private static final long serialVersionUID = 1675962692054005355L;
  protected final StateToStateAction toStateAction;
  protected final Predictor predictor;
  protected final Action[] actions;
  @Monitor
  protected final double[] actionValues;
  protected Action bestAction;
  @Monitor
  private double bestValue;
  private RealVector lastUpdate = null;

  public Greedy(Predictor predictor, Action[] actions, StateToStateAction toStateAction) {
    this.toStateAction = toStateAction;
    this.predictor = predictor;
    this.actions = actions;
    actionValues = new double[actions.length];
  }

  @Override
  public Action decide(RealVector s) {
    return computeBestAction(s);
  }

  public Action computeBestAction(RealVector s_tp1) {
    if (s_tp1 == null)
      return null;
    if (lastUpdate == s_tp1)
      return bestAction;
    updateActionValues(s_tp1);
    findBestAction();
    lastUpdate = s_tp1;
    return bestAction;
  }

  private void findBestAction() {
    bestValue = actionValues[0];
    bestAction = actions[0];
    for (int i = 1; i < actions.length; i++) {
      double value = actionValues[i];
      if (value > bestValue) {
        bestValue = value;
        bestAction = actions[i];
      }
    }
  }

  private void updateActionValues(RealVector s_tp1) {
    for (int i = 0; i < actions.length; i++) {
      RealVector phi_sa = toStateAction.stateAction(s_tp1, actions[i]);
      actionValues[i] = predictor.predict(phi_sa);
    }
  }

  @Override
  public double pi(RealVector s, Action a) {
    computeBestAction(s);
    return a == bestAction ? 1 : 0;
  }

  public StateToStateAction toStateAction() {
    return toStateAction;
  }

  public Action bestAction() {
    return bestAction;
  }

  public double bestActionValue() {
    return bestValue;
  }

  @Override
  public double[] values() {
    return actionValues;
  }

  @Override
  public Action[] actions() {
    return actions;
  }
}
