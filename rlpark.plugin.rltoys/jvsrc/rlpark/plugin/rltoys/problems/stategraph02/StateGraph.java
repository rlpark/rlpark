package rlpark.plugin.rltoys.problems.stategraph02;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.Action;

public class StateGraph implements Serializable {
  private static final long serialVersionUID = -2849828765062029412L;
  private final State[] states;
  private final Map<State, Integer> stateToIndex = new LinkedHashMap<State, Integer>();
  private final Map<Action, double[][]> transitions = new LinkedHashMap<Action, double[][]>();

  public StateGraph(State s0, State[] states, Action[] actions) {
    this.states = states;
    for (int i = 0; i < states.length; i++)
      stateToIndex.put(states[i], i);
    for (int i = 0; i < actions.length; i++)
      transitions.put(actions[i], newMatrix(states.length));
  }

  private double[][] newMatrix(int length) {
    double[][] matrix = new double[length][];
    for (int i = 0; i < matrix.length; i++)
      matrix[i] = new double[matrix.length];
    return matrix;
  }

  public int nbStates() {
    return states.length;
  }

  public int indexOf(State s) {
    return stateToIndex.get(s);
  }

  public State sampleNextState(Random random, State s, Action a) {
    double[] p_sa = transitions.get(a)[stateToIndex.get(s)];
    double randomValue = random.nextDouble();
    int i = -1;
    double sum = 0;
    do {
      i++;
      sum += p_sa[i];
    } while (sum < randomValue && i < p_sa.length - 1);
    assert sum > 0;
    return states[i];
  }

  public boolean isTerminal(State s) {
    int s_i = stateToIndex.get(s);
    for (double[][] ps : transitions.values()) {
      if (sum(ps[s_i]) == 0)
        return true;
    }
    return false;
  }

  private double sum(double[] ds) {
    double sum = 0;
    for (double p : ds)
      sum += p;
    return sum;
  }

  public void addTransition(State s_t, Action a_t, State s_tp1, double prob) {
    transitions.get(a_t)[stateToIndex.get(s_t)][stateToIndex.get(s_tp1)] = prob;
  }

  public boolean checkDistribution() {
    for (double[][] psa : transitions.values()) {
      for (double[] ps : psa) {
        double sum = sum(ps);
        if (sum != 0 && sum != 1.0)
          return false;
      }
    }
    return true;
  }
}
