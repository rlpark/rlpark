package rlpark.plugin.rltoys.algorithms.representations.actions;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class StateToStateActionBuffer implements StateToStateAction {
  private static final long serialVersionUID = -2580625240502199207L;
  private final StateToStateAction toStateAction;
  private final LinkedHashMap<RealVector, Map<Action, RealVector>> buffer = new LinkedHashMap<RealVector, Map<Action, RealVector>>();
  private final Queue<RealVector> stateQueue = new LinkedList<RealVector>();
  private final int capacity;

  public StateToStateActionBuffer(StateToStateAction toStateAction) {
    this(toStateAction, 10);
  }

  public StateToStateActionBuffer(StateToStateAction toStateAction, int capacity) {
    this.toStateAction = toStateAction;
    this.capacity = capacity;
  }

  @Override
  public RealVector stateAction(RealVector s, Action a) {
    if (s == null || a == null)
      return toStateAction.stateAction(s, a);
    if (buffer.size() > capacity) {
      RealVector oldestState = stateQueue.poll();
      buffer.remove(oldestState);
    }
    Map<Action, RealVector> actionToFeatures = buffer.get(s);
    if (actionToFeatures == null) {
      stateQueue.add(s);
      actionToFeatures = new LinkedHashMap<Action, RealVector>();
      buffer.put(s, actionToFeatures);
    }
    RealVector phi_sa = actionToFeatures.get(a);
    if (phi_sa == null) {
      phi_sa = toStateAction.stateAction(s, a);
      actionToFeatures.put(a, phi_sa);
    }
    return phi_sa;
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
