package rlpark.plugin.rltoys.problems.stategraph02;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.policy.SingleActionPolicy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;

public class TrackingProblem {
  static public final double Gamma = .9;
  static public final double SameStateProbability = .99;
  static public final State A = new State("A", -1.0);
  static public final State B = new State("B", 1.0);
  static public final State C = new State("C", -3.0);
  static public Action Move = new Action() {
    private static final long serialVersionUID = -4236679466464277389L;
  };
  static public final Policy acting = new SingleActionPolicy(Move);

  static class TrackingProjector implements Projector {
    private static final long serialVersionUID = 6604066132865938651L;
    private final BVector stateVector;

    public TrackingProjector(StateGraph stateGraph, int nbApproximatedStates) {
      stateVector = new BVector(stateGraph.nbStates() - nbApproximatedStates + 1);
    }

    @Override
    public RealVector project(double[] obs) {
      stateVector.clear();
      stateVector.setOn(Math.min((int) obs[0], stateVector.getDimension() - 2));
      stateVector.setOn(stateVector.getDimension() - 1);
      assert stateVector.nonZeroElements() == 2;
      return stateVector;
    }

    @Override
    public int vectorSize() {
      return stateVector.getDimension();
    }

    @Override
    public double vectorNorm() {
      return 2;
    }

  };

  static public GraphProblem create(Random random) {
    StateGraph stateGraph = new StateGraph(A, new State[] { A, B, C }, new Action[] { Move });
    stateGraph.addTransition(A, Move, A, SameStateProbability);
    stateGraph.addTransition(A, Move, B, (1 - SameStateProbability) / 2);
    stateGraph.addTransition(A, Move, C, (1 - SameStateProbability) / 2);
    stateGraph.addTransition(B, Move, B, SameStateProbability);
    stateGraph.addTransition(C, Move, C, SameStateProbability);
    stateGraph.addTransition(B, Move, A, 1 - SameStateProbability);
    stateGraph.addTransition(C, Move, A, 1 - SameStateProbability);
    return new GraphProblem(random, A, stateGraph, new TrackingProjector(stateGraph, 1));
  }
}
