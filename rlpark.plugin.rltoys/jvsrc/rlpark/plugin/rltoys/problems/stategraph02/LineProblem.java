package rlpark.plugin.rltoys.problems.stategraph02;

import java.util.Random;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.policy.SingleActionPolicy;

public class LineProblem {
  static public final double Gamma = .9;
  public static final double Reward = 1.0;
  static public final State A = new State("A", 0.0);
  static public final State B = new State("B", 0.0);
  static public final State C = new State("C", 0.0);
  static public final State D = new State("D", Reward);
  static public Action Move = new Action() {
    private static final long serialVersionUID = -4236679466464277389L;
  };
  static public final Policy acting = new SingleActionPolicy(Move);

  static public GraphProblem create(Random random) {
    StateGraph stateGraph = new StateGraph(A, new State[] { A, B, C, D }, new Action[] { Move });
    stateGraph.addTransition(A, Move, B, 1.0);
    stateGraph.addTransition(B, Move, C, 1.0);
    stateGraph.addTransition(C, Move, D, 1.0);
    return new GraphProblem(random, A, stateGraph, new MarkovProjector(stateGraph));
  }
}