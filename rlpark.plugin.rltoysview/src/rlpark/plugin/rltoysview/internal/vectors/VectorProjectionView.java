package rlpark.plugin.rltoysview.internal.vectors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import rlpark.plugin.rltoys.agents.functions.VectorProjected2D;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.viewable.ContinuousFunction2D;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.core.internal.views.helpers.ScreenShotAction;
import zephyr.plugin.plotting.internal.axes.Axes;
import zephyr.plugin.plotting.internal.heatmap.ColorMapAction;
import zephyr.plugin.plotting.internal.heatmap.Function2DDrawer;
import zephyr.plugin.plotting.internal.heatmap.FunctionSampler;
import zephyr.plugin.plotting.internal.heatmap.MapData;

@SuppressWarnings("restriction")
public class VectorProjectionView extends ForegroundCanvasView<VectorProjected2D> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(ContinuousFunction2D.class);
    }

    @Override
    protected boolean isInstanceSupported(Object instance) {
      return (instance instanceof VectorProjected2D);
    }
  }

  private final Colors colors = new Colors();
  private final Function2DDrawer valueFunctionDrawer = new Function2DDrawer(colors);
  private final Axes axes = new Axes();
  private final ColorMapAction colorMapAction = new ColorMapAction(this, valueFunctionDrawer);
  private MapData functionData;
  private FunctionSampler functionSampler;

  @Override
  protected void paint(GC gc) {
    axes.updateScaling(gc.getClipping());
    valueFunctionDrawer.paint(gc, canvas, functionData, true);
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return isSupported(instance);
  }

  static boolean isSupported(Object instance) {
    return (instance instanceof VectorProjected2D) || (instance instanceof RealVector);
  }

  @Override
  protected boolean synchronize(VectorProjected2D projectedVector) {
    functionSampler.updateData(functionData);
    return true;
  }

  @Override
  public void onInstanceSet(Clock clock, VectorProjected2D projectedVector) {
    super.onInstanceSet(clock, projectedVector);
    functionData = new MapData(200);
    functionSampler = new FunctionSampler(new FunctionAdapter(projectedVector));
    updateAxes(projectedVector);
  }

  private void updateAxes(VectorProjected2D function) {
    axes.x.reset();
    axes.x.update(function.minX());
    axes.x.update(function.maxX());
    axes.y.reset();
    axes.y.update(function.minY());
    axes.y.update(function.maxY());
  }

  @Override
  protected void setToolbar(IToolBarManager toolbarManager) {
    toolbarManager.add(new ScreenShotAction(this));
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
  public void onInstanceUnset(Clock clock) {
    super.onInstanceUnset(clock);
    valueFunctionDrawer.unset();
  }
}
