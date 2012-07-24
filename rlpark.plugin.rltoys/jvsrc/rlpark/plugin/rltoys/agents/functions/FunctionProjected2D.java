package rlpark.plugin.rltoys.agents.functions;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.math.ranges.Range;

public abstract class FunctionProjected2D {
  protected final Projector projector;
  protected final Range xRange;
  protected final Range yRange;

  public FunctionProjected2D(Projector projector, Range xRange, Range yRange) {
    this.projector = projector;
    this.xRange = xRange;
    this.yRange = yRange;
  }

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