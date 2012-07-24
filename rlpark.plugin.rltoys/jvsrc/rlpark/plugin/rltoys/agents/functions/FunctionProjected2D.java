package rlpark.plugin.rltoys.agents.functions;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.math.vector.RealVector;

public abstract class FunctionProjected2D {
  protected final Projector projector;
  protected final Range xRange;
  protected final Range yRange;

  public FunctionProjected2D(Projector projector, Range xRange, Range yRange) {
    this.projector = projector;
    this.xRange = xRange;
    this.yRange = yRange;
  }

  public double value(double x, double y) {
    return value(projector.project(new double[] { x, y }));
  }

  abstract double value(RealVector project);

  public double minX() {
    return xRange.min();
  }

  public double maxX() {
    return xRange.max();
  }

  public double minY() {
    return yRange.min();
  }

  public double maxY() {
    return yRange.max();
  }
}