package rlpark.plugin.rltoysview.internal.continuousgridworld;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import rltoys.environments.continuousgridworld.ContinuousFunction;
import rltoys.environments.continuousgridworld.ContinuousGridworld;
import rltoys.math.ranges.Range;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.axes.Axes;

public class ContinuousGridworldView extends ForegroundCanvasView<ContinuousGridworld> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(ContinuousGridworld.class);
    }

    @Override
    protected boolean isInstanceSupported(Object instance) {
      return isSupported(instance);
    }
  }

  public class TickListener implements Listener<Clock> {
    @SuppressWarnings("synthetic-access")
    @Override
    public void listen(Clock eventInfo) {
      trajectory.append(instance.current().currentPosition());
    }
  }

  private final Colors colors = new Colors();
  private final Axes axes = new Axes();
  final Trajectory trajectory = new Trajectory();
  private final Listener<Clock> listener = new TickListener();
  private final Function2DDrawer rewardDrawer = new Function2DDrawer(colors, axes);

  @Override
  protected void paint(GC gc) {
    axes.updateScaling(gc.getClipping());
    rewardDrawer.paint(gc, canvas, 4);
    drawTrajectory(gc);
  }

  private void drawTrajectory(GC gc) {
    int lineSize = ZephyrPlotting.preferredLineSize();
    int extremities = lineSize * 6;
    gc.setForeground(colors.color(gc, Colors.COLOR_BLACK));
    gc.setBackground(colors.color(gc, Colors.COLOR_BLACK));
    gc.setLineWidth(lineSize);
    Point lastPoint = null;
    for (float[] position : trajectory.getData()) {
      Point point = position != null ? axes.toG(position[0], position[1]) : null;
      if (lastPoint != null && point == null)
        gc.fillRectangle(point.x - (extremities / 2), point.y - (extremities / 2), extremities, extremities);
      if (lastPoint != null && point != null)
        gc.drawLine(lastPoint.x, lastPoint.y, point.x, point.y);
      lastPoint = point;
    }
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return isSupported(instance);
  }

  static boolean isSupported(Object instance) {
    if (!(instance instanceof ContinuousGridworld))
      return false;
    return ((ContinuousGridworld) instance).nbDimensions() == 2;
  }

  @Override
  protected boolean synchronize() {
    ContinuousFunction rewardFunction = instance.current().rewardFunction();
    if (rewardFunction == null)
      return true;
    rewardDrawer.synchronize(rewardFunction);
    return true;
  }

  @Override
  public void onInstanceSet() {
    super.onInstanceSet();
    trajectory.setLength(1000);
    instance.clock().onTick.connect(listener);
    updateAxes();
    rewardDrawer.reset();
  }

  private void updateAxes() {
    Range[] ranges = instance.current().getObservationRanges();
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
    instance.clock().onTick.disconnect(listener);
    rewardDrawer.reset();
  }
}
