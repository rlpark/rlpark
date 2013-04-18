package rlpark.plugin.rltoys.problems.stategraph;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policies;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;


public abstract class FiniteStateGraph implements Serializable {
  private static final long serialVersionUID = 50902147743062052L;

  static public class StepData {
    public final int stepTime;
    public final GraphState s_t;
    public final Action a_t;
    public final double r_tp1;
    public final GraphState s_tp1;
    public final Action a_tp1;

    public StepData(int stepTime, GraphState s_t, Action a_t, GraphState s_tp1, double r_tp1, Action a_tp1) {
      assert s_t != null || a_t == null;
      this.stepTime = stepTime;
      this.s_t = s_t;
      this.a_t = a_t;
      this.s_tp1 = s_tp1;
      this.r_tp1 = r_tp1;
      this.a_tp1 = a_tp1;
    }

    public RealVector v_t() {
      return s_t != null ? s_t.v() : null;
    }

    public RealVector v_tp1() {
      return s_tp1 != null ? s_tp1.v() : null;
    }

    @Override
    public boolean equals(Object obj) {
      if (super.equals(obj))
        return true;
      StepData other = (StepData) obj;
      return stepTime == other.stepTime && s_t == other.s_t && a_t == other.a_t && s_tp1 == other.s_tp1
          && r_tp1 == other.r_tp1;
    }

    @Override
    public int hashCode() {
      return toString().hashCode();
    }

    @Override
    public String toString() {
      return String.format("%d: %s,%s -> %s", stepTime, s_t, a_t, s_tp1);
    }
  }

  private int stepTime = -1;
  private GraphState s_0;
  private Action a_t;
  private GraphState s_t;
  private final GraphState[] states;
  private final Policy acting;

  public FiniteStateGraph(Policy policy, GraphState[] states) {
    this.states = states;
    acting = policy;
    for (int i = 0; i < states.length; i++)
      states[i].setVectorRepresentation(BVector.toBVector(states.length, new int[] { i }));
  }

  protected void setInitialState(GraphState s_0) {
    assert this.s_0 == null;
    assert s_0 != null;
    this.s_0 = s_0;
  }

  public StepData step() {
    stepTime += 1;
    GraphState s_tm1 = s_t;
    Action a_tm1 = null;
    if (s_t == null)
      s_t = s_0;
    else {
      a_tm1 = a_t;
      s_t = s_tm1.nextState(a_tm1);
    }
    a_t = Policies.decide(acting, s_t.v());
    double r_t = s_t.reward;
    if (!s_t.hasNextState()) {
      a_t = null;
      s_t = null;
    }
    return new StepData(stepTime, s_tm1, a_tm1, s_t, r_t, a_t);
  }

  abstract public double gamma();

  abstract public double[] expectedDiscountedSolution();

  public GraphState[] states() {
    return states;
  }

  public int nbStates() {
    return states.length;
  }

  public GraphState currentState() {
    return s_t;
  }

  abstract public Action[] actions();

  public GraphState initialState() {
    return s_0;
  }

  public Policy policy() {
    return acting;
  }

  public int indexOf(GraphState s) {
    for (int i = 0; i < states.length; i++)
      if (states[i] == s)
        return i;
    return -1;
  }

  public GraphState state(RealVector s) {
    if (s == null)
      return null;
    return states[((BVector) s).getActiveIndexes()[0]];
  }
}
