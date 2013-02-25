package rlpark.plugin.rltoys.algorithms.control.acting;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.DiscreteActionPolicy;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.policy.PolicyPrototype;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class Greedy implements DiscreteActionPolicy, PolicyPrototype {
  private static final long serialVersionUID = 1675962692054005355L;
  protected final StateToStateAction toStateAction;
  protected final Predictor predictor;
  protected final Action[] actions;
  @Monitor
  protected final double[] actionValues;
  protected Action bestAction;
  @Monitor
  private double bestValue;

  public Greedy(Predictor predictor, Action[] actions, StateToStateAction toStateAction) {
    this.toStateAction = toStateAction;
    this.predictor = predictor;
    this.actions = actions;
    actionValues = new double[actions.length];
  }

  @Override
  public Action sampleAction() {
    return bestAction;
  }

  @Override
  public void update(RealVector x_tp1) {
    updateActionValues(x_tp1);
    findBestAction();
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
  public double pi(Action a) {
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

  @Override
  public Policy duplicate() {
    return new Greedy(predictor, actions, Utils.clone(toStateAction));
  }
}
