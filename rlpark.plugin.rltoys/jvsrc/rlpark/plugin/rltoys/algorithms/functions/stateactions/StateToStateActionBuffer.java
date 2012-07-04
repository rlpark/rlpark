package rlpark.plugin.rltoys.algorithms.functions.stateactions;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class StateToStateActionBuffer implements StateToStateAction {
  private static final long serialVersionUID = -2580625240502199207L;
  protected final Map<Action, Integer> actionToIndex = new LinkedHashMap<Action, Integer>();
  private final StateToStateAction toStateAction;
  private final RealVector[] stateActionBuffered;
  private RealVector lastFeatureVector = null;

  public StateToStateActionBuffer(StateToStateAction toStateAction, Action[] actions) {
    this.toStateAction = toStateAction;
    for (int i = 0; i < actions.length; i++)
      actionToIndex.put(actions[i], i);
    stateActionBuffered = new RealVector[actions.length];
  }

  @Override
  public RealVector stateAction(RealVector x, Action a) {
    if (x == null || a == null)
      return toStateAction.stateAction(x, a);
    if (x != lastFeatureVector) {
      Arrays.fill(stateActionBuffered, null);
      lastFeatureVector = x;
    }
    int a_i = actionToIndex.get(a);
    if (stateActionBuffered[a_i] == null)
      stateActionBuffered[a_i] = toStateAction.stateAction(x, a);
    return stateActionBuffered[a_i];
  }

  @Override
  public double vectorNorm() {
    return toStateAction.vectorNorm();
  }

  @Override
  public int vectorSize() {
    return toStateAction.vectorSize();
  }

  public StateToStateAction toStateAction() {
    return toStateAction;
  }
}
