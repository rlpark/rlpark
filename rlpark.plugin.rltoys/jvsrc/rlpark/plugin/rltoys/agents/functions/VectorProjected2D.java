package rlpark.plugin.rltoys.agents.functions;


import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.problems.ProblemBounded;

public class VectorProjected2D extends FunctionProjected2D {
  private RealVector vector;

  public VectorProjected2D(Projector projector, ProblemBounded problem) {
    super(projector, problem.getObservationRanges()[0], problem.getObservationRanges()[1]);
    assert problem.getObservationRanges().length == 2;
  }

  @Override
  double value(RealVector projected) {
    return vector.dotProduct(projected);
  }
}
