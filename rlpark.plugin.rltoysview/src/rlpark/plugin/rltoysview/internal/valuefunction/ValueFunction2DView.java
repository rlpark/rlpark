package rlpark.plugin.rltoysview.internal.valuefunction;

import org.eclipse.swt.graphics.GC;

import rltoys.algorithms.representations.ValueFunction2D;
import rltoys.math.ranges.Range;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.axes.Axes;
import zephyr.plugin.plotting.heatmap.Function2DDrawer;
import zephyr.plugin.plotting.heatmap.Interval;

public class ValueFunction2DView extends ForegroundCanvasView<ValueFunction2D> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(ValueFunction2D.class);
    }

    @Override
    protected boolean isInstanceSupported(Object instance) {
      return isSupported(instance);
    }
  }

  private final Colors colors = new Colors();
  private final Axes axes = new Axes();
  private final Function2DDrawer valueFunctionDrawer = new Function2DDrawer(colors);

  @Override
  protected void paint(GC gc) {
    axes.updateScaling(gc.getClipping());
    valueFunctionDrawer.paint(gc, canvas);
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return isSupported(instance);
  }

  static boolean isSupported(Object instance) {
    return (instance instanceof ValueFunction2D);
  }

  @Override
  protected boolean synchronize() {
    valueFunctionDrawer.synchronize();
    return true;
  }

  @Override
  public void onInstanceSet() {
    super.onInstanceSet();
    ValueFunction2D valueFunction = instance.current();
    Range[] ranges = valueFunction.ranges();
    Interval xRange = new Interval(ranges[0].min(), ranges[0].max());
    Interval yRange = new Interval(ranges[1].min(), ranges[1].max());
    valueFunctionDrawer.set(xRange, yRange, valueFunction, 200);
    updateAxes();
  }

  private void updateAxes() {
    Range[] ranges = instance.current().ranges();
    axes.x.reset();
    axes.y.reset();
    axes.x.update(ranges[0].min());
    axes.x.update(ranges[0].max());
    axes.y.update(ranges[1].min());
    axes.y.update(ranges[1].max());
  }

  @Override
  public void onInstanceUnset() {
    super.onInstanceUnset();
    valueFunctionDrawer.unset();
  }
}
