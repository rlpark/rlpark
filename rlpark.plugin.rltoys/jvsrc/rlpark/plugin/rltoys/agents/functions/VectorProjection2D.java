package rlpark.plugin.rltoys.agents.functions;


import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.problems.ProblemBounded;
import rlpark.plugin.rltoys.problems.ProblemDiscreteAction;

public class VectorProjection2D extends FunctionProjected2D {
  private final StateToStateAction toStateAction;
  private final Action[] actions;

  public VectorProjection2D(ProblemBounded problem, Projector projector) {
    this(problem, projector, null);
  }

  public VectorProjection2D(ProblemBounded problem, Projector projector, StateToStateAction toStateAction) {
    super(projector, problem.getObservationRanges()[0], problem.getObservationRanges()[1]);
    assert problem.getObservationRanges().length == 2;
    this.toStateAction = toStateAction;
    actions = toStateAction != null ? ((ProblemDiscreteAction) problem).actions() : null;
  }

  public double value(RealVector vector, double x, double y) {
    RealVector projected = projector.project(new double[] { x, y });
    if (projected.getDimension() == vector.getDimension())
      return vector.dotProduct(projected);
    double sum = 0.0;
    for (Action a : actions)
      sum += vector.dotProduct(toStateAction.stateAction(projected, a));
    return sum;
  }
}
