package rlpark.plugin.rltoysview.internal.puddleworld;

import rlpark.plugin.rltoys.algorithms.functions.ContinuousFunction;
import zephyr.plugin.core.api.viewable.ContinuousFunction2D;
import zephyr.plugin.plotting.internal.heatmap.Interval;

@SuppressWarnings("restriction")
public class ContinuousFunction2DAdapter implements ContinuousFunction2D {
  private final ContinuousFunction function;
  private final Interval xRange;
  private final Interval yRange;

  public ContinuousFunction2DAdapter(ContinuousFunction function, Interval xRange, Interval yRange) {
    this.function = function;
    this.xRange = xRange;
    this.yRange = yRange;
  }

  @Override
  public double value(double x, double y) {
    return function.value(new double[] { x, y });
  }

  @Override
  public double minX() {
    return xRange.min;
  }

  @Override
  public double maxX() {
    return xRange.max;
  }

  @Override
  public double minY() {
    return yRange.min;
  }

  @Override
  public double maxY() {
    return yRange.max;
  }
}
