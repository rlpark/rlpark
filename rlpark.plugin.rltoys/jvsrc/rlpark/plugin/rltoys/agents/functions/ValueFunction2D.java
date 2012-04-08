package rlpark.plugin.rltoys.agents.functions;

import java.awt.geom.Point2D;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.ProblemBounded;
import zephyr.plugin.core.api.viewable.ContinuousFunction2D;

public class ValueFunction2D implements ContinuousFunction2D {
  private final Projector projector;
  private final Predictor predictor;
  private final ProblemBounded problem;
  private final Range xRange;
  private final Range yRange;

  public ValueFunction2D(Projector projector, ProblemBounded problem, Predictor predictor) {
    this.projector = projector;
    this.problem = problem;
    assert problem.getObservationRanges().length == 2;
    this.predictor = predictor;
    xRange = problem.getObservationRanges()[0];
    yRange = problem.getObservationRanges()[1];
  }

  @Override
  public double value(double x, double y) {
    return predictor.predict(projector.project(new double[] { x, y }));
  }

  @Override
  public Point2D position() {
    TRStep step = problem.lastStep();
    if (step == null || step.o_tp1 == null)
      return null;
    return new Point2D.Double(step.o_tp1[0], step.o_tp1[1]);
  }

  @Override
  public double minX() {
    return xRange.min();
  }

  @Override
  public double maxX() {
    return xRange.max();
  }

  @Override
  public double minY() {
    return yRange.min();
  }

  @Override
  public double maxY() {
    return yRange.max();
  }
}
