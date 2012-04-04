package rlpark.plugin.rltoys.problems.stategraph;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;


public class GraphState {

  public final String name;
  public final Double reward;

  final private Map<Action, GraphState> transitions = new LinkedHashMap<Action, GraphState>();
  private RealVector vectorRepresentation;

  public GraphState(String name, double reward) {
    this.name = name;
    this.reward = reward;
  }

  public void connect(Action action, GraphState state) {
    transitions.put(action, state);
  }

  protected GraphState nextState(Action action) {
    return transitions.get(action);
  }

  public boolean hasNextState() {
    return !transitions.isEmpty();
  }

  @Override
  public String toString() {
    return name;
  }

  public Collection<GraphState> children() {
    return transitions.values();
  }

  public void setVectorRepresentation(RealVector vectorRepresentation) {
    this.vectorRepresentation = vectorRepresentation;
  }

  public RealVector v() {
    return vectorRepresentation;
  }
}
