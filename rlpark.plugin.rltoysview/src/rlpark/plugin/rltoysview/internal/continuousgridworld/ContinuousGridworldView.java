package rlpark.plugin.rltoysview.internal.continuousgridworld;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.continuousgridworld.ContinuousGridworld;
import rlpark.plugin.rltoys.problems.continuousgridworld.NormalizedFunction;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.viewable.ContinuousFunction;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.core.internal.views.helpers.ScreenShotAction;
import zephyr.plugin.plotting.internal.axes.Axes;
import zephyr.plugin.plotting.internal.heatmap.ColorMapAction;
import zephyr.plugin.plotting.internal.heatmap.Function2DDrawer;
import zephyr.plugin.plotting.internal.heatmap.FunctionSampler;
import zephyr.plugin.plotting.internal.heatmap.Interval;
import zephyr.plugin.plotting.internal.heatmap.MapData;

@SuppressWarnings("restriction")
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

  private final Colors colors = new Colors();
  private final Axes axes = new Axes();
  private final EpisodeTrajectories episodeTrajectories = new EpisodeTrajectories();
  private final Function2DDrawer rewardDrawer = new Function2DDrawer(colors);
  private final ColorMapAction colorMapAction = new ColorMapAction(this, rewardDrawer);
  private float[][][] trajectories = null;
  private MapData rewardData;

  @Override
  protected void paint(GC gc) {
    axes.updateScaling(gc.getClipping());
    rewardDrawer.paint(gc, canvas, rewardData, false);
    ContinuousGridworld current = instance.current();
    drawStartPosition(gc, current);
    drawTrajectory(gc);
  }

  @Override
  protected void setToolbar(IToolBarManager toolbarManager) {
    toolbarManager.add(new ScreenShotAction(this));
    toolbarManager.add(colorMapAction);
  }

  private void drawStartPosition(GC gc, ContinuousGridworld current) {
    double[] start = current.start();
    if (start == null)
      return;
    int lineSize = ZephyrPlotting.preferredLineWidth();
    gc.setBackground(colors.color(gc, rewardDrawer.spriteColor()));
    int size = lineSize * 6;
    gc.fillOval(axes.toGX(start[0]) - (size / 2), axes.toGY(start[1]) - (size / 2), size, size);
  }

  private void drawTrajectory(GC gc) {
    if (trajectories == null)
      return;
    int lineSize = ZephyrPlotting.preferredLineWidth();
    int extremities = lineSize * 6;
    gc.setForeground(colors.color(gc, rewardDrawer.spriteColor()));
    gc.setBackground(colors.color(gc, rewardDrawer.spriteColor()));
    gc.setLineWidth(lineSize);
    for (float[][] trajectory : trajectories) {
      if (trajectory.length == 0)
        continue;
      Point lastPoint = null;
      for (float[] position : trajectory) {
        if (position == null)
          break;
        Point point = axes.toG(position[0], position[1]);
        if (lastPoint != null)
          gc.drawLine(lastPoint.x, lastPoint.y, point.x, point.y);
        lastPoint = point;
      }
      gc.fillRectangle(lastPoint.x - (extremities / 2), lastPoint.y - (extremities / 2), extremities, extremities);
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
    if (rewardData == null)
      synchronizeRewardFunction();
    trajectories = episodeTrajectories.copyTrajectories();
    return true;
  }

  private void synchronizeRewardFunction() {
    final ContinuousGridworld problem = instance.current();
    ContinuousFunction rewardFunction = problem.rewardFunction();
    rewardData = new MapData(200);
    if (rewardFunction != null) {
      if (rewardFunction instanceof NormalizedFunction)
        rewardFunction = ((NormalizedFunction) rewardFunction).function();
      Range[] ranges = problem.getObservationRanges();
      Interval xRange = new Interval(ranges[0].min(), ranges[0].max());
      Interval yRange = new Interval(ranges[1].min(), ranges[1].max());
      FunctionSampler sampler = new FunctionSampler(xRange, yRange, rewardFunction);
      sampler.updateData(rewardData);
    }
    updateAxes();
  }

  @Override
  public void onInstanceSet() {
    super.onInstanceSet();
    episodeTrajectories.connect(instance.current(), instance.clock());
    trajectories = null;
    rewardData = null;
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    colorMapAction.init(memento);
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    colorMapAction.saveState(memento);
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
    episodeTrajectories.disconnect();
    rewardDrawer.unset();
    trajectories = null;
    rewardData = null;
  }
}
