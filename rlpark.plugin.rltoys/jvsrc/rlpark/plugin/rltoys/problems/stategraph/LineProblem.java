package rlpark.plugin.rltoys.problems.stategraph;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.ActionPolicy;
import rlpark.plugin.rltoys.envio.policy.Policy;

@SuppressWarnings("serial")
public class LineProblem extends FiniteStateGraph {
  static public final GraphState A = new GraphState("A", 0.0);
  static public final GraphState B = new GraphState("B", 0.0);
  static public final GraphState C = new GraphState("C", 0.0);
  static public final GraphState D = new GraphState("D", 1.0);
  static private final GraphState[] states = { A, B, C, D };
  static public Action Move = new Action() {
  };
  static private final Policy acting = new ActionPolicy(Move);

  static {
    A.connect(Move, B);
    B.connect(Move, C);
    C.connect(Move, D);
  }

  public LineProblem() {
    super(acting, states);
    setInitialState(A);
  }

  @Override
  public double[] expectedDiscountedSolution() {
    return new double[] { Math.pow(0.9, 2), Math.pow(0.9, 1), Math.pow(0.9, 0) };
  }

  @Override
  public Action[] actions() {
    return new Action[] { Move };
  }
}