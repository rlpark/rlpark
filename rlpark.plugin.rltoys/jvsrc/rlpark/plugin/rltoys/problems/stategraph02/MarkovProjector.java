package rlpark.plugin.rltoys.problems.stategraph02;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;

public class MarkovProjector implements Projector {
  private static final long serialVersionUID = -4778217022247397306L;
  private final BVector stateVector;

  public MarkovProjector(StateGraph stateGraph) {
    stateVector = new BVector(stateGraph.nbStates());
  }

  @Override
  public RealVector project(double[] obs) {
    stateVector.clear();
    stateVector.setOn((int) obs[0]);
    return stateVector;
  }

  @Override
  public int vectorSize() {
    return stateVector.getDimension();
  }

  @Override
  public double vectorNorm() {
    return 1;
  }
}
