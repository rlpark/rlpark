package rlpark.plugin.rltoysview.internal.valuefunction;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import rltoys.algorithms.representations.ValueFunction2D;
import rltoys.math.ranges.Range;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.views.helpers.ForegroundCanvasView;
import zephyr.plugin.plotting.axes.Axes;
import zephyr.plugin.plotting.heatmap.ColorMapAction;
import zephyr.plugin.plotting.heatmap.Function2DDrawer;
import zephyr.plugin.plotting.heatmap.FunctionSampler;
import zephyr.plugin.plotting.heatmap.Interval;
import zephyr.plugin.plotting.heatmap.MapData;

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

  static private final int PositionSize = 4;
  private final Colors colors = new Colors();
  private final Function2DDrawer valueFunctionDrawer = new Function2DDrawer(colors);
  private final Axes axes = new Axes();
  private final ColorMapAction colorMapAction = new ColorMapAction(this, valueFunctionDrawer);
  private double[] position;
  private MapData valueFunctionData;
  private FunctionSampler valueFunctionSampler;

  @Override
  protected void paint(GC gc) {
    axes.updateScaling(gc.getClipping());
    valueFunctionDrawer.paint(gc, canvas, valueFunctionData, true);
    if (position != null)
      drawPosition(gc);
  }

  private void drawPosition(GC gc) {
    gc.setBackground(colors.color(gc, valueFunctionDrawer.spriteColor()));
    gc.fillOval(axes.toGX(position[0]) - (PositionSize / 2), axes.toGY(position[1]) - (PositionSize / 2), PositionSize,
                PositionSize);
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
    valueFunctionSampler.updateData(valueFunctionData);
    position = instance.current().position();
    return true;
  }

  @Override
  public void onInstanceSet() {
    super.onInstanceSet();
    ValueFunction2D valueFunction = instance.current();
    valueFunctionData = new MapData(200);
    Range[] ranges = valueFunction.ranges();
    Interval xRange = new Interval(ranges[0].min(), ranges[0].max());
    Interval yRange = new Interval(ranges[1].min(), ranges[1].max());
    valueFunctionSampler = new FunctionSampler(xRange, yRange, valueFunction);
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
  protected void setToolbar(IToolBarManager toolbarManager) {
    toolbarManager.add(colorMapAction);
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

  @Override
  public void onInstanceUnset() {
    super.onInstanceUnset();
    valueFunctionDrawer.unset();
  }
}
