package rlpark.plugin.rltoysview.internal.puddleworld;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import rlpark.plugin.rltoys.algorithms.functions.ContinuousFunction;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.puddleworld.NormalizedFunction;
import rlpark.plugin.rltoys.problems.puddleworld.PuddleWorld;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.core.internal.views.helpers.ScreenShotAction;
import zephyr.plugin.plotting.internal.axes.Axes;
import zephyr.plugin.plotting.internal.heatmap.ColorMapAction;
import zephyr.plugin.plotting.internal.heatmap.Function2DBufferedDrawer;
import zephyr.plugin.plotting.internal.heatmap.FunctionSampler;
import zephyr.plugin.plotting.internal.heatmap.Interval;
import zephyr.plugin.plotting.internal.heatmap.MapData;

@SuppressWarnings("restriction")
public class PuddleWorldView extends ForegroundCanvasView<PuddleWorld> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(PuddleWorld.class);
    }

    @Override
    protected boolean isInstanceSupported(Object instance) {
      return isSupported(instance);
    }
  }

  private final Colors colors = new Colors();
  private final Axes axes = new Axes();
  private final EpisodeTrajectories episodeTrajectories = new EpisodeTrajectories();
  private final Function2DBufferedDrawer rewardDrawer = new Function2DBufferedDrawer(colors);
  private final ColorMapAction colorMapAction = new ColorMapAction(this, rewardDrawer);
  private float[][][] trajectories = null;
  private MapData rewardData;
  private double[] startPosition;

  @Override
  protected void paint(GC gc) {
    axes.updateScaling(gc.getClipping());
    rewardDrawer.paint(gc, canvas, rewardData, false);
    drawStartPosition(gc, startPosition);
    drawTrajectory(gc);
  }

  @Override
  protected void setToolbar(IToolBarManager toolbarManager) {
    toolbarManager.add(new ScreenShotAction(this));
    toolbarManager.add(colorMapAction);
  }

  private void drawStartPosition(GC gc, double[] start) {
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
        Point point = axes.toG(position[0], position[1]);
        if (lastPoint != null)
          gc.drawLine(lastPoint.x, lastPoint.y, point.x, point.y);
        lastPoint = point;
      }
      if (lastPoint != null)
        gc.fillRectangle(lastPoint.x - (extremities / 2), lastPoint.y - (extremities / 2), extremities, extremities);
    }
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return isSupported(instance);
  }

  static boolean isSupported(Object instance) {
    if (!(instance instanceof PuddleWorld))
      return false;
    return ((PuddleWorld) instance).nbDimensions() == 2;
  }

  @Override
  protected boolean synchronize(PuddleWorld current) {
    if (rewardData == null)
      synchronizeRewardFunction(current);
    trajectories = episodeTrajectories.copyTrajectories();
    return true;
  }

  private void synchronizeRewardFunction(PuddleWorld problem) {
    ContinuousFunction rewardFunction = problem.rewardFunction();
    rewardData = new MapData(200);
    if (rewardFunction != null) {
      if (rewardFunction instanceof NormalizedFunction)
        rewardFunction = ((NormalizedFunction) rewardFunction).function();
      Range[] ranges = problem.getObservationRanges();
      Interval xRange = new Interval(ranges[0].min(), ranges[0].max());
      Interval yRange = new Interval(ranges[1].min(), ranges[1].max());
      ContinuousFunction2DAdapter rewardFunctionAdapter = new ContinuousFunction2DAdapter(rewardFunction, xRange,
                                                                                          yRange);
      FunctionSampler sampler = new FunctionSampler(rewardFunctionAdapter);
      sampler.updateData(rewardData);
    }
    updateAxes(problem);
  }

  @Override
  public void onInstanceSet(Clock clock, PuddleWorld problem) {
    super.onInstanceSet(clock, problem);
    startPosition = problem.start();
    episodeTrajectories.connect(problem, clock);
    trajectories = null;
    rewardData = null;
    setViewName();
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

  private void updateAxes(PuddleWorld problem) {
    Range[] ranges = problem.getObservationRanges();
    axes.x.reset();
    axes.y.reset();
    axes.x.update(ranges[0].min());
    axes.x.update(ranges[0].max());
    axes.y.update(ranges[1].min());
    axes.y.update(ranges[1].max());
  }

  @Override
  public void onInstanceUnset(Clock clock) {
    super.onInstanceUnset(clock);
    episodeTrajectories.disconnect();
    rewardDrawer.unset();
    trajectories = null;
    rewardData = null;
    startPosition = null;
  }
}
