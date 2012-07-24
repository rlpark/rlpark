package rlpark.plugin.rltoysview.internal.vectors;

import rlpark.plugin.rltoys.agents.functions.VectorProjected2D;
import zephyr.plugin.core.api.viewable.ContinuousFunction2D;

public class FunctionAdapter implements ContinuousFunction2D {
  private final VectorProjected2D projectedVector;

  public FunctionAdapter(VectorProjected2D projectedVector) {
    this.projectedVector = projectedVector;
  }

  @Override
  public double value(double x, double y) {
    return projectedVector.value(x, y);
  }

  @Override
  public double minX() {
    return projectedVector.minX();
  }

  @Override
  public double maxX() {
    return projectedVector.maxX();
  }

  @Override
  public double minY() {
    return projectedVector.minY();
  }

  @Override
  public double maxY() {
    return projectedVector.maxY();
  }
}
