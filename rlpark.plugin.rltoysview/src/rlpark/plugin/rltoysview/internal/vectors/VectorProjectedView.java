package rlpark.plugin.rltoysview.internal.vectors;

import java.util.Arrays;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.GC;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import rlpark.plugin.rltoys.agents.functions.VectorProjection2D;
import rlpark.plugin.rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;
import zephyr.plugin.core.internal.views.helpers.ScreenShotAction;
import zephyr.plugin.plotting.internal.actions.EnableScaleAction;
import zephyr.plugin.plotting.internal.actions.SynchronizeAction;
import zephyr.plugin.plotting.internal.axes.Axes;
import zephyr.plugin.plotting.internal.heatmap.ColorMapAction;
import zephyr.plugin.plotting.internal.heatmap.Function2DBufferedDrawer;
import zephyr.plugin.plotting.internal.heatmap.FunctionSampler;
import zephyr.plugin.plotting.internal.heatmap.MapData;

@SuppressWarnings("restriction")
public class VectorProjectedView extends ForegroundCanvasView<VectorProjection2D> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(VectorProjection2D.class);
    }
  }

  protected final EnableScaleAction centerAction = new EnableScaleAction();
  private final Colors colors = new Colors();
  private final Function2DBufferedDrawer valueFunctionDrawer = new Function2DBufferedDrawer(colors);
  private final Axes axes = new Axes();
  private final ColorMapAction colorMapAction = new ColorMapAction(this, valueFunctionDrawer);
  private final SynchronizeAction synchronizeAction = new SynchronizeAction();
  private final MapData functionData = new MapData(200);
  private FunctionSampler functionSampler;
  private final VectorAdapter adapter = new VectorAdapter();

  public VectorProjectedView() {
    adapter.layoutFunctionSet.connect(new Listener<ClassNode>() {
      @Override
      public void listen(ClassNode classNode) {
        if (classNode == null)
          setDefaultName();
        else
          setViewName(classNode.label(), Arrays.toString(classNode.path()));
      }
    });
  }

  @Override
  protected void paint(GC gc) {
    if (!adapter.layoutFunctionIsSet()) {
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
    if (centerAction.scaleEnabled())
      functionSampler.resetRange();
    if (!synchronizeAction.synchronizedData())
      return false;
    RealVector projected = adapter.lockLayoutFunction();
    if (projected != null)
      functionSampler.updateData(functionData);
    adapter.unlockLayoutFunction();
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
  public boolean[] provide(CodeNode[] codeNodes) {
    if (codeNode() == codeNodes[0])
      return CodeTrees.toBooleans(codeNodes, -1);
    return super.provide(codeNodes);
  }

  @Override
  protected void setToolbar(IToolBarManager toolbarManager) {
    toolbarManager.add(centerAction);
    toolbarManager.add(new ScreenShotAction(this));
    toolbarManager.add(colorMapAction);
    toolbarManager.add(synchronizeAction);
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    colorMapAction.init(memento);
    adapter.init(memento);
    synchronizeAction.init(memento);
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    colorMapAction.saveState(memento);
    adapter.saveState(memento);
    synchronizeAction.saveState(memento);
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
      adapter.setLayoutFunction(classNode);
  }
}
