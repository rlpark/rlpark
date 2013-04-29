package rlpark.plugin.rltoys.agents.functions;


import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class VectorProjection2D extends FunctionProjected2D {
  private final StateToStateAction toStateAction;
  private final Action[] actions;

  public VectorProjection2D(Range xRange, Range yRange, Projector projector) {
    this(xRange, yRange, projector, null, null);
  }

  public VectorProjection2D(Range xRange, Range yRange, Projector projector, StateToStateAction toStateAction,
      Action[] actions) {
    super(projector, xRange, yRange);
    this.toStateAction = toStateAction;
    this.actions = actions;
  }

  public double value(RealVector vector, double x, double y) {
    RealVector projected = projector.project(new double[] { x, y });
    if (projected.getDimension() == vector.getDimension())
      return vector.dotProduct(projected);
    double sum = 0.0;
    if (toStateAction == null)
      return sum;
    for (Action a : actions)
      sum += vector.dotProduct(toStateAction.stateAction(projected, a));
    return sum;
  }
}
