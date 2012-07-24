package rlpark.plugin.rltoysview.internal.vectors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import rlpark.plugin.rltoys.agents.functions.VectorProjection2D;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
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
public class VectorProjectedView extends ForegroundCanvasView<VectorProjection2D> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(VectorProjection2D.class);
    }
  }

  private final Colors colors = new Colors();
  private final Function2DDrawer valueFunctionDrawer = new Function2DDrawer(colors);
  private final Axes axes = new Axes();
  private final ColorMapAction colorMapAction = new ColorMapAction(this, valueFunctionDrawer);
  private final MapData functionData = new MapData(200);
  private FunctionSampler functionSampler;
  private final FunctionAdapter adapter = new FunctionAdapter();

  @Override
  protected void paint(GC gc) {
    if (!adapter.canProject()) {
      defaultPainting(gc);
      return;
    }
    axes.updateScaling(gc.getClipping());
    valueFunctionDrawer.paint(gc, canvas, functionData, true);
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return (instance instanceof VectorProjection2D) || (instance instanceof RealVector);
  }

  @Override
  protected boolean synchronize(VectorProjection2D projection) {
    RealVector projected = adapter.lockProjected();
    if (projected != null)
      functionSampler.updateData(functionData);
    adapter.unlockProjected();
    return true;
  }

  @Override
  public void onInstanceSet(Clock clock, VectorProjection2D projectedVector) {
    super.onInstanceSet(clock, projectedVector);
    adapter.setProjection(projectedVector);
    functionSampler = new FunctionSampler(adapter);
    updateAxes(projectedVector);
  }

  private void updateAxes(VectorProjection2D function) {
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
    adapter.init(memento);
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    colorMapAction.saveState(memento);
    adapter.saveState(memento);
  }

  @Override
  public void onInstanceUnset(Clock clock) {
    super.onInstanceUnset(clock);
    valueFunctionDrawer.unset();
  }

  @Override
  public void drop(CodeNode[] supported) {
    ClassNode classNode = (ClassNode) supported[0];
    Object instance = classNode.instance();
    if (instance instanceof VectorProjection2D) {
      super.drop(supported);
      return;
    }
    if (instance instanceof RealVector)
      adapter.setProjected(classNode);
  }
}
