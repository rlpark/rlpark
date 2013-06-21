package rlpark.plugin.rltoys.problems.stategraph;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.policy.SingleActionPolicy;

@SuppressWarnings("serial")
public class LineProblem extends FiniteStateGraph {
  static private final double Gamma = .9;
  static public final GraphState A = new GraphState("A", 0.0);
  static public final GraphState B = new GraphState("B", 0.0);
  static public final GraphState C = new GraphState("C", 0.0);
  static public final GraphState D = new GraphState("D", 1.0);
  static private final GraphState[] states = { A, B, C, D };
  static public Action Move = new Action() {
  };
  static private final Policy acting = new SingleActionPolicy(Move);

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
    return new double[] { Math.pow(Gamma, 2), Math.pow(Gamma, 1), Math.pow(Gamma, 0) };
  }

  @Override
  public Action[] actions() {
    return new Action[] { Move };
  }

  @Override
  public double gamma() {
    return Gamma;
  }
}