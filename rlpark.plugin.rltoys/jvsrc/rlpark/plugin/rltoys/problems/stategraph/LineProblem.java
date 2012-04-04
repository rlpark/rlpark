/**
 * 
 */
package rlpark.plugin.rltoys.problems.stategraph;

import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class LineProblem extends FiniteStateGraph {
  private static final long serialVersionUID = 1519617702412222535L;
  static public final GraphState A = new GraphState("A", 0.0);
  static public final GraphState B = new GraphState("B", 0.0);
  static public final GraphState C = new GraphState("C", 0.0);
  static public final GraphState D = new GraphState("D", 1.0);
  static private final GraphState[] states = { A, B, C, D };
  static Action a = new Action() {
    private static final long serialVersionUID = 5767853896667312578L;
  };
  static private final Policy acting = new Policy() {
    private static final long serialVersionUID = 4629299146091181748L;

    @Override
    public double pi(RealVector s, Action a_s) {
      return a_s == a ? 1 : 0;
    }

    @Override
    public Action decide(RealVector s) {
      return a;
    }
  };

  static {
    A.connect(a, B);
    B.connect(a, C);
    C.connect(a, D);
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
    return new Action[] { a };
  }
}