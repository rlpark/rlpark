package rlpark.plugin.robot.internal.statemachine;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.rltoys.utils.Utils;
import zephyr.plugin.core.api.signals.Signal;

public class StateMachine<T> {
  public final Signal<StateMachine<T>> onEnd = new Signal<StateMachine<T>>();
  public final Signal<StateMachine<T>> onStateChange = new Signal<StateMachine<T>>();
  private int currentStateIndex;
  private final List<StateNode<T>> nodes;
  private boolean stateChanged = false;

  public StateMachine(StateNode<T>... nodes) {
    this(Utils.asList(nodes));
  }

  public StateMachine(List<? extends StateNode<T>> nodes) {
    currentStateIndex = 0;
    this.nodes = new ArrayList<StateNode<T>>(nodes);
  }

  public void step(T t) {
    if (currentStateIndex >= nodes.size()) {
      currentStateIndex = 0;
      onEnd.fire(this);
    }
    StateNode<T> currentState = nodes.get(currentStateIndex);
    if (stateChanged) {
      currentState.start();
      stateChanged = false;
    }
    currentState.step(t);
    if (currentState.isDone()) {
      onStateChange.fire(this);
      stateChanged = true;
      currentStateIndex++;
    }
  }

  public StateNode<T> currentState() {
    if (currentStateIndex < nodes.size())
      return nodes.get(currentStateIndex);
    return null;
  }

  public void setNodes(List<StateNode<T>> nodes) {
    this.nodes.clear();
    this.nodes.addAll(nodes);
  }
}
