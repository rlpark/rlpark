package rlpark.plugin.rltoys.problems.stategraph;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.ConstantPolicy;
import rlpark.plugin.rltoys.envio.policy.Policy;

@SuppressWarnings("serial")
public class RandomWalk extends FiniteStateGraph {
  static private final double Gamma = .9;
  static public final GraphState TL = new GraphState("TL", 0.0);
  static public final GraphState A = new GraphState("A", 0.0);
  static public final GraphState B = new GraphState("B", 0.0);
  static public final GraphState C = new GraphState("C", 0.0);
  static public final GraphState D = new GraphState("D", 0.0);
  static public final GraphState E = new GraphState("E", 0.0);
  static public final GraphState TR = new GraphState("TR", 1.0);
  static public final Action Left = new Action() {
    @Override
    public String toString() {
      return "left";
    };
  };
  static public final Action Right = new Action() {
    @Override
    public String toString() {
      return "right";
    };
  };

  static {
    A.connect(Left, TL);
    A.connect(Right, B);

    B.connect(Left, A);
    B.connect(Right, C);

    C.connect(Left, B);
    C.connect(Right, D);

    D.connect(Left, C);
    D.connect(Right, E);

    E.connect(Left, D);
    E.connect(Right, TR);
  }

  public RandomWalk(Random random) {
    this(newPolicy(random, 0.5));
  }


  public RandomWalk(Policy policy) {
    super(policy, new GraphState[] { TL, A, B, C, D, E, TR });
    setInitialState(C);
  }

  @Override
  public double[] expectedDiscountedSolution() {
    return new double[] { 0.056, 0.140, 0.258, 0.431, 0.644 };
  }

  public static ConstantPolicy newPolicy(Random random, double leftProbability) {
    return new ConstantPolicy(random, new Action[] { Left, Right },
                              new double[] { leftProbability, 1 - leftProbability });
  }

  @Override
  public Action[] actions() {
    return new Action[] { Left, Right };
  }


  @Override
  public double gamma() {
    return Gamma;
  }
}
