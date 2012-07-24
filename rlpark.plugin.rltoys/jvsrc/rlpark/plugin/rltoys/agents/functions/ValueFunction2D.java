package rlpark.plugin.rltoys.agents.functions;


import java.awt.geom.Point2D;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.problems.ProblemBounded;
import zephyr.plugin.core.api.viewable.ContinuousFunction2D;
import zephyr.plugin.core.api.viewable.PositionFunction2D;

public class ValueFunction2D extends FunctionProjected2D implements ContinuousFunction2D, PositionFunction2D {
  final Predictor predictor;
  final ProblemBounded problem;

  public ValueFunction2D(Projector projector, ProblemBounded problem, Predictor predictor) {
    super(projector, problem.getObservationRanges()[0], problem.getObservationRanges()[1]);
    assert problem.getObservationRanges().length == 2;
    this.predictor = predictor;
    this.problem = problem;
  }

  @Override
  public Point2D position() {
    TRStep step = problem.lastStep();
    if (step == null || step.o_tp1 == null)
      return null;
    return new Point2D.Double(step.o_tp1[0], step.o_tp1[1]);
  }

  @Override
  public double value(double x, double y) {
    return predictor.predict(projector.project(new double[] { x, y }));
  }
}
